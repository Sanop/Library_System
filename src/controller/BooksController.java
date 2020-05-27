package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import util.BooksTM;

import javax.management.Notification;
import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class BooksController {

    public JFXButton btnAddNew;
    public JFXButton btnDelete;
    public JFXButton btnSave;
    public JFXTextField txtISBN;
    public JFXTextField txtBookName;
    public JFXTextField txtAuthor;
    public JFXTextField txtPrice;
    public TableView<BooksTM> tblBookDetails;
    public AnchorPane root;

    public void initialize(){

        TranslateTransition tt = new TranslateTransition();
        tt.setDuration(Duration.millis(500));
        tt.setNode(root);
        tt.setFromX(-10);
        tt.setToX(0);
        tt.play();

        loadTable();

        txtISBN.setDisable(true);
        txtBookName.setDisable(true);
        txtAuthor.setDisable(true);
        txtPrice.setDisable(true);


        btnDelete.setDisable(true);
        btnSave.setDisable(true);
        btnAddNew.requestFocus();

        tblBookDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tblBookDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("book_Name"));
        tblBookDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tblBookDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));

        tblBookDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BooksTM>() {
            @Override
            public void changed(ObservableValue<? extends BooksTM> observable, BooksTM oldValue, BooksTM newValue) {

                int selectedIndex = tblBookDetails.getSelectionModel().getSelectedIndex();
                if(selectedIndex == -1){
                    return;
                }

                BooksTM selectedItem = tblBookDetails.getSelectionModel().getSelectedItem();
                txtISBN.setText(selectedItem.getIsbn());
                txtBookName.setText(selectedItem.getBook_Name());
                txtAuthor.setText(selectedItem.getAuthor());
                txtPrice.setText(selectedItem.getPrice()+"");


                txtBookName.setDisable(false);
                txtAuthor.setDisable(false);
                txtPrice.setDisable(false);

                btnSave.setText("Update");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
            }
        });


    }

    public void btnAddNewOnAction(ActionEvent event) {

        txtISBN.setDisable(false);
        txtBookName.setDisable(false);
        txtAuthor.setDisable(false);
        txtPrice.setDisable(false);

        txtISBN.clear();
        txtPrice.clear();
        txtBookName.clear();
        txtAuthor.clear();

        txtISBN.requestFocus();

        btnSave.setDisable(false);
        btnSave.setText("Save");
    }

    public void btnDeleteOnAction(ActionEvent event) {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to delete this book..?").showAndWait();
        if(buttonType.get() == ButtonType.OK){
            delete();
            Image img = new Image("image/icons8-delete-48.png");
            Notifications notifications = Notifications.create()
                    .title("Confirm")
                    .text("Delete from Database")
                    .graphic(new ImageView(img))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.BOTTOM_RIGHT);
            notifications.show();
        }

    }

    public void btnSaveOnAction(ActionEvent event) {
        saveOnAction();
    }

    public void loadTable(){
        ObservableList<BooksTM> items = tblBookDetails.getItems();

        Connection connection = DBConnection.getInstance().getConnection();
        items.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from books where status = 'NotIssued'");
            while (resultSet.next()){
                String isbn = resultSet.getString(1);
                String name = resultSet.getString(2);
                String author = resultSet.getString(3);
                String price = resultSet.getString(4);

                items.add(new BooksTM(isbn,name,author,Double.parseDouble(price)));
            }

            tblBookDetails.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        String isbn = txtISBN.getText();
        String name = txtBookName.getText();
        String author = txtAuthor.getText();
        String price = txtPrice.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into books values(?,?,?,?,?)");
            preparedStatement.setObject(1,isbn);
            preparedStatement.setObject(2,name);
            preparedStatement.setObject(3,author);
            preparedStatement.setObject(4,price);
            preparedStatement.setObject(5,"NotIssued");

            int i = preparedStatement.executeUpdate();
            if(i!=0){
                Image img = new Image("image/icons8-checked-radio-button-48.png");
                Notifications notifications = Notifications.create().title("Completed").
                        text("Save to Database")
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.BOTTOM_RIGHT);
                notifications.show();
                common();
                btnSave.setText("Save");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("delete from books where isbn = ?");
            preparedStatement.setObject(1,txtISBN.getText());
            preparedStatement.executeUpdate();

            txtPrice.clear();
            txtAuthor.clear();
            txtBookName.clear();
            txtISBN.clear();

            btnAddNew.requestFocus();
            btnSave.setDisable(true);
            btnDelete.setDisable(true);
            loadTable();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(){
        String isbn = txtISBN.getText();
        String name = txtBookName.getText();
        String author = txtAuthor.getText();
        String price = txtPrice.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update books set book_Name = ?,author = ?,price = ? where isbn = ?");
            preparedStatement.setObject(1,name);
            preparedStatement.setObject(2,author);
            preparedStatement.setObject(3,price);
            preparedStatement.setObject(4,isbn);

            int i = preparedStatement.executeUpdate();
            if(i!=0){
                Image img = new Image("image/icons8-checked-radio-button-48.png");
                Notifications notifications = Notifications.create()
                        .title("Completed")
                        .text("Update Database ")
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.BOTTOM_RIGHT);
                notifications.show();
                common();
                btnSave.setText("Save");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void common(){
        txtISBN.setDisable(true);
        txtBookName.setDisable(true);
        txtAuthor.setDisable(true);
        txtPrice.setDisable(true);

        txtPrice.clear();
        txtAuthor.clear();
        txtBookName.clear();
        txtISBN.clear();

        btnAddNew.requestFocus();
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        loadTable();
    }

    public void saveOnAction(){
        if(txtISBN.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields Can not be empty").showAndWait();
            txtISBN.requestFocus();
        }else if(txtBookName.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields Can not be empty").showAndWait();
            txtBookName.requestFocus();
        }else if(txtAuthor.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields Can not be empty").showAndWait();
            txtAuthor.requestFocus();
        }else if(txtPrice.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields Can not be empty").showAndWait();
            txtPrice.requestFocus();
        }else if(txtPrice.getText().matches("\\d+[.]\\d+")){
            if(btnSave.getText().equals("Save")){
                save();
            }else{
                update();
            }

        }else{
            new Alert(Alert.AlertType.ERROR,"Please Enter correct Format..\nEg:-(XXXX.XX)").showAndWait();
            txtPrice.requestFocus();
        }

        loadTable();
    }

    public void txtPriceOnAction(ActionEvent event) {
        saveOnAction();
    }

    public void imgHomeOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Console.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage)this.root.getScene().getWindow();
        stage.setScene(scene);
        Image imge = new Image("/image/icons8-books-48.png");
        stage.getIcons().add(imge);
        stage.setTitle("Console");
        stage.centerOnScreen();
    }
}
