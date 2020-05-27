package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import util.ReturnTM;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReturnController {
    public JFXTextField txtBorrowerID;
    public TableView<ReturnTM> tblReturn;
    public AnchorPane root;
    public JFXTextField txtBorrowerName;
    public JFXTextField txtIsbn;
    public JFXTextField txtTitle;
    public JFXTextField txtReturnDate;
    public JFXTextField txtLatePayment;
    public JFXButton btnReturn;

    public void initialize(){
        TranslateTransition tt = new TranslateTransition();
        tt.setDuration(Duration.millis(500));
        tt.setNode(root);
        tt.setFromX(-10);
        tt.setToX(0);
        tt.play();

        btnReturn.setDisable(true);
        txtLatePayment.setDisable(true);

        tblReturn.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tblReturn.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tblReturn.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("lendDate"));
        tblReturn.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        tblReturn.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("status"));

        tblReturn.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ReturnTM>() {
            @Override
            public void changed(ObservableValue<? extends ReturnTM> observable, ReturnTM oldValue, ReturnTM newValue) {
                try {
                    ReturnTM selectedItem = tblReturn.getSelectionModel().getSelectedItem();
                    if (selectedItem.getStatus().equals("Return")) {
                        new Alert(Alert.AlertType.ERROR,"This book is already Returned by you").showAndWait();
                        return;
                    }
                    txtIsbn.setText(selectedItem.getIsbn());
                    txtTitle.setText(selectedItem.getTitle());
                    txtReturnDate.setText(selectedItem.getReturnDate());

                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String format = now.format(dateTimeFormatter);
                    Date toDay = Date.valueOf(format);
                    Date returnDate = Date.valueOf(txtReturnDate.getText());

                    if (toDay.before(returnDate)) {
                        txtLatePayment.setDisable(true);
                    } else {
                        txtLatePayment.setDisable(false);
                        txtLatePayment.requestFocus();
                    }
                }catch(NullPointerException e){

                }
                btnReturn.setDisable(false);
            }
        });
    }

    public void txtBorrowerIDOnAction(ActionEvent event) {
        searchBorrower();
    }

    public void searchBorrower(){
        Connection connection = DBConnection.getInstance().getConnection();
        ObservableList<ReturnTM> items = tblReturn.getItems();
        items.clear();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from lendDetails where borrower_ID = ?");
            PreparedStatement preparedStatement1 = connection.prepareStatement("select book_Name from books where isbn =?");
            preparedStatement.setObject(1,txtBorrowerID.getText());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                do{
                    String borrower_ID = resultSet.getString(1);
                    String isbn = resultSet.getString(2);
                    String lendDate = resultSet.getString(3);
                    String returnDate = resultSet.getString(4);
                    String status = resultSet.getString(5);

                    preparedStatement1.setObject(1,isbn);
                    ResultSet resultSet1 = preparedStatement1.executeQuery();
                    resultSet1.next();
                    String name = resultSet1.getString(1);

                    items.add(new ReturnTM(isbn,name,lendDate,returnDate,status));
                }while(resultSet.next());

                PreparedStatement preparedStatement2 = connection.prepareStatement("select borrower_Name from borrowers where borrower_ID = ?");
                preparedStatement2.setObject(1,txtBorrowerID.getText());
                ResultSet resultSet1 = preparedStatement2.executeQuery();
                resultSet1.next();
                String name = resultSet1.getString(1);
                txtBorrowerName.setText(name);

                tblReturn.refresh();
            }else {
                new Alert(Alert.AlertType.ERROR,"Can not Find Member\nplease enter correct ID").showAndWait();
                txtBorrowerID.requestFocus();
                txtBorrowerID.clear();
                txtBorrowerName.clear();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnReturnOnAction(ActionEvent event) {

        Connection connection = DBConnection.getInstance().getConnection();
        if(txtBorrowerID.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields can not be empty").showAndWait();
            txtBorrowerID.requestFocus();
        }else if(txtIsbn.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields can not be empty").showAndWait();
            tblReturn.requestFocus();
        }else{
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("update books set status = 'NotIssued' where isbn = ? ");
                preparedStatement.setObject(1,txtIsbn.getText());
                preparedStatement.executeUpdate();

                PreparedStatement preparedStatement1 = connection.prepareStatement("update lendDetails set status = 'Return' where isbn = ?");
                preparedStatement1.setObject(1,txtIsbn.getText());
                preparedStatement1.executeUpdate();

                if(txtLatePayment.isDisable()){
                    PreparedStatement preparedStatement2 = connection.prepareStatement("insert into returnDetails (isbn,borrower_ID,date_Of_Lend,date_Of_Return) values (?,?,?,?) ");
                    preparedStatement2.setObject(1,txtIsbn.getText());
                    preparedStatement2.setObject(2,txtBorrowerID.getText());

                    PreparedStatement preparedStatement3 = connection.prepareStatement("select date_Of_Lend from lendDetails where isbn = ?");
                    preparedStatement3.setObject(1,txtIsbn.getText());
                    ResultSet resultSet = preparedStatement3.executeQuery();
                    resultSet.next();
                    String date_Of_Lend = resultSet.getString(1);

                    preparedStatement2.setObject(3,date_Of_Lend);
                    preparedStatement2.setObject(4,txtReturnDate.getText());
                    int i = preparedStatement2.executeUpdate();
                    if(i != 0){
                        btnReturn.setDisable(true);
                    }

                    loadTable();

                    txtIsbn.clear();
                    txtTitle.clear();
                    txtReturnDate.clear();
                    txtBorrowerID.requestFocus();
                }else{
                    if(txtLatePayment.getText().trim().isEmpty()){
                        new Alert(Alert.AlertType.ERROR,"Please Add Late Payment").showAndWait();
                        txtLatePayment.requestFocus();
                        return;
                    }else if(txtLatePayment.getText().trim().matches("\\d+[.]\\d{2}$")){
                        if(txtLatePayment.getText().trim().contains("..")){
                            new Alert(Alert.AlertType.ERROR,"Please Enter Payment in correct Format\nEx:-(xx.xx)").showAndWait();
                            txtLatePayment.requestFocus();
                        }else{
                            PreparedStatement preparedStatement2 = connection.prepareStatement("insert into returnDetails (isbn,borrower_ID,date_Of_Lend,date_Of_Return,late_Payment,real_Date_Of_Return) values (?,?,?,?,?,?) ");
                            preparedStatement2.setObject(1,txtIsbn.getText());
                            preparedStatement2.setObject(2,txtBorrowerID.getText());

                            PreparedStatement preparedStatement3 = connection.prepareStatement("select date_Of_Lend from lendDetails where isbn = ?");
                            preparedStatement3.setObject(1,txtIsbn.getText());
                            ResultSet resultSet = preparedStatement3.executeQuery();
                            resultSet.next();
                            String date_Of_Lend = resultSet.getString(1);

                            preparedStatement2.setObject(3,date_Of_Lend);
                            preparedStatement2.setObject(4,txtReturnDate.getText());
                            preparedStatement2.setObject(5,txtLatePayment.getText());

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDateTime ldt = LocalDateTime.now();
                            String format = dtf.format(ldt);
                            preparedStatement2.setObject(6,format);
                            int i = preparedStatement2.executeUpdate();

                            if(i != 0 ){
                                btnReturn.setDisable(true);
                            }

                            loadTable();

                            txtIsbn.clear();
                            txtTitle.clear();
                            txtReturnDate.clear();
                            txtBorrowerID.requestFocus();
                            txtLatePayment.clear();
                            txtLatePayment.setDisable(true);
                        }
                    }else{
                        new Alert(Alert.AlertType.ERROR,"Please Enter Payment in correct Format\nEx:-(xx.xx)").showAndWait();
                        txtLatePayment.requestFocus();
                    }

                }
                Image img = new Image("image/icons8-checked-radio-button-48.png");
                Notifications notifications = Notifications.create()
                        .title("Success")
                        .text("Save to Database")
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.BOTTOM_RIGHT);
                notifications.show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void imgHomeOnClicked(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Console.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage)this.root.getScene().getWindow();
        stage.setScene(scene);
        Image imge = new Image("/image/icons8-books-48.png");
        stage.getIcons().add(imge);
        stage.setTitle("Console");
        stage.centerOnScreen();
    }

    public void loadTable(){
        ObservableList<ReturnTM> items = tblReturn.getItems();
        items.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from lendDetails where borrower_ID = ?");

            preparedStatement.setObject(1,txtBorrowerID.getText());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String borrowerID = resultSet.getString(1);
                String isbn = resultSet.getString(2);
                String date_Of_Lend = resultSet.getString(3);
                String date_Of_Return = resultSet.getString(4);
                String status = resultSet.getString(5);

                PreparedStatement preparedStatement1 = connection.prepareStatement("select book_Name from books where isbn =? ");
                preparedStatement1.setObject(1,isbn);
                ResultSet resultSet1 = preparedStatement1.executeQuery();

                resultSet1.next();
                String name = resultSet1.getString(1);

                items.add(new ReturnTM(isbn,name,date_Of_Lend,date_Of_Return,status));

            }
            tblReturn.refresh();
        } catch (SQLException e) {

        }
    }
}
