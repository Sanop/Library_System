package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
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
import javafx.scene.control.Label;
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
import util.BorrowerTM;
import util.LendTM;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LendController {
    public TableView<BorrowerTM> tblBorrowerDetail;
    public AnchorPane root;
    public JFXTextField txtBorrowID;
    public JFXTextField txtBorrowerName;
    public JFXTextField txtIsbn;
    public TableView<BooksTM> tblBooks;
    public JFXTextField txtBookName;
    public Label lblToday;
    public JFXDatePicker clndrReturn;
    public TableView<LendTM> tblDetails;
    public JFXButton btnSave;
    public JFXButton btnAddToTable;
    public static int count = 0;

    public void initialize() {
        TranslateTransition tt = new TranslateTransition();
        tt.setDuration(Duration.millis(500));
        tt.setNode(root);
        tt.setFromX(-10);
        tt.setToX(0);
        tt.play();

        tblBorrowerDetail.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("borrower_ID"));
        tblBorrowerDetail.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("borrower_Name"));

        tblBooks.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tblBooks.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("book_Name"));

        tblDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("borrowerID"));
        tblDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("isbn"));
        tblDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("lendDate"));
        tblDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        loadBorrowersTable();
        loadBookTable();
        loadDate();


        btnSave.setDisable(true);

        tblBorrowerDetail.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BorrowerTM>() {
            @Override
            public void changed(ObservableValue<? extends BorrowerTM> observable, BorrowerTM oldValue, BorrowerTM newValue) {
                int selectedIndex = tblBorrowerDetail.getSelectionModel().getSelectedIndex();
                if (selectedIndex == -1) {
                    return;
                }

                BorrowerTM selectedItem = tblBorrowerDetail.getSelectionModel().getSelectedItem();
                Connection connection = DBConnection.getInstance().getConnection();

                try {
                    int count = 0;
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from lendDetails where status = 'Borrowed' and borrower_ID = '" + selectedItem.getBorrower_ID() + "'");
                    while (resultSet.next()){
                        count++;
                    }
                    if(count <= 1){
                        txtBorrowerName.setText(selectedItem.getBorrower_Name());
                        txtBorrowID.setText(selectedItem.getBorrower_ID());
                    }else{
                        new Alert(Alert.AlertType.ERROR,"This member is already take two books").showAndWait();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        tblBooks.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BooksTM>() {
            @Override
            public void changed(ObservableValue<? extends BooksTM> observable, BooksTM oldValue, BooksTM newValue) {
                int selectedIndex = tblBooks.getSelectionModel().getSelectedIndex();
                if (selectedIndex == -1) {
                    return;
                }

                BooksTM selectedItem = tblBooks.getSelectionModel().getSelectedItem();
                txtIsbn.setText(selectedItem.getIsbn());
                txtBookName.setText(selectedItem.getBook_Name());
            }
        });
    }

    public void txtBorrowerIdOnAction(ActionEvent event) {
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select borrower_ID,borrower__Name from borrowers where borrower_ID = '" + txtBorrowID.getText() + "'");
            if (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                ObservableList<BorrowerTM> items = tblBorrowerDetail.getItems();
                items.clear();
                items.add(new BorrowerTM(id, name));
                tblBorrowerDetail.refresh();
            } else {
                new Alert(Alert.AlertType.ERROR, "Can not Find Borrower like this id").showAndWait();
                txtBorrowID.requestFocus();
                txtBorrowID.selectAll();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadBorrowersTable() {
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from borrowers");
            ObservableList<BorrowerTM> items = tblBorrowerDetail.getItems();
            items.clear();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                items.add(new BorrowerTM(id, name));
            }
            tblBorrowerDetail.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void txtIsbnOnAction(ActionEvent event) {
        String text = txtIsbn.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select isbn,book_Name from books where isbn = ?");
            preparedStatement.setObject(1, text);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String isbn = resultSet.getString(1);
                String name = resultSet.getString(2);
                ObservableList<BooksTM> items = tblBooks.getItems();
                items.clear();
                items.add(new BooksTM(isbn, name));
                tblBooks.refresh();
            } else {
                new Alert(Alert.AlertType.ERROR, "Can not Find Book with this isbn").showAndWait();
                txtIsbn.requestFocus();
                txtIsbn.selectAll();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadBookTable() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from books where status = 'NotIssued'");
            ObservableList<BooksTM> items = tblBooks.getItems();
            items.clear();

            while (resultSet.next()) {
                String isbn = resultSet.getString(1);
                String name = resultSet.getString(2);
                items.add(new BooksTM(isbn, name));
            }
            tblBooks.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        lblToday.setText(date.format(dtf));
    }

    public void btnSaveOnAction(ActionEvent event) {
        ObservableList<LendTM> items = tblDetails.getItems();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
<<<<<<< HEAD
            PreparedStatement preparedStatement = connection.prepareStatement("insert into lendDetails (borrower_ID,isbn,date_Of_Lend,date_Of_Return,status)" +
                    "values (?,?,?,?,?)");
=======
            PreparedStatement preparedStatement = connection.prepareStatement("insert into lendDetails (borrower_ID,isbn,date_Of_Lend,date_Of_Return)" +
                    "values (?,?,?,?)");
>>>>>>> 41cc5a4fee3074071bf497c4d04a5997654d2be7

            PreparedStatement preparedStatement1 = connection.prepareStatement("update books set status = 'Issued' where isbn = ?");



            for (int i = 0; i < items.size(); i++) {

                LendTM lendTM = items.get(i);

                String borrowerID = lendTM.getBorrowerID();
                String isbn = lendTM.getIsbn();
                String lendDate = lendTM.getLendDate();
                String returnDate = lendTM.getReturnDate();

                preparedStatement.setObject(1, borrowerID);
                preparedStatement.setObject(2, isbn);
                preparedStatement.setObject(3, lendDate);
                preparedStatement.setObject(4, returnDate);
<<<<<<< HEAD
                preparedStatement.setObject(5,"Not Borrowed");
=======
>>>>>>> 41cc5a4fee3074071bf497c4d04a5997654d2be7

                preparedStatement1.setObject(1,isbn);

                preparedStatement.executeUpdate();
                preparedStatement1.executeUpdate();

            }

            new Alert(Alert.AlertType.CONFIRMATION, "Success").showAndWait();
            Image img = new Image("image/icons8-checked-radio-button-48.png");
            Notifications notifications = Notifications.create()
                    .title("Confirm")
                    .text("Save to Database")
                    .graphic(new ImageView(img))
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.TOP_RIGHT);
            notifications.show();
            txtBorrowID.clear();
            txtBorrowID.setDisable(false);
            txtIsbn.clear();
            txtBookName.clear();
            txtBorrowerName.clear();
            txtBorrowerName.setDisable(false);
            txtBorrowID.requestFocus();

            items.clear();
            tblDetails.refresh();
            tblBorrowerDetail.setDisable(false);
            btnAddToTable.setDisable(false);
            loadBookTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnAddToTableOnAction(ActionEvent event) {
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select borrower_ID from lendDetails where status = 'Borrowed'");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                count++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if(count == 2){
            new Alert(Alert.AlertType.ERROR,"This Member has already take maximum number of books").showAndWait();
            btnAddToTable.setDisable(true);
            return;
        }
        ObservableList<LendTM> items1 = tblDetails.getItems();
        int size = items1.size();
        if(size <= 1){
            if (txtBorrowID.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Fields can not be Empty").showAndWait();
                txtBorrowID.requestFocus();
            } else if (txtIsbn.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Fields can not be Empty").showAndWait();
                txtIsbn.requestFocus();
            } else {
                LocalDate value = clndrReturn.getValue();
                String s = value + "";
                System.out.println(s);
                if(!(s.equals("null"))){
                    ObservableList<LendTM> items = tblDetails.getItems();
                    String isbn = txtIsbn.getText();
                    String borrowerID = txtBorrowID.getText();
                    String today = lblToday.getText();


                    for (int i = 0; i < items.size(); i++) {
                        LendTM lendTM = items.get(i);
                        String isbn1 = lendTM.getIsbn();
                        if(isbn1.equals(isbn)){
                            new Alert(Alert.AlertType.ERROR,"This book is alredy order by you").showAndWait();
                            tblBooks.requestFocus();
                            txtBookName.clear();
                            txtIsbn.clear();
                            return;
                        }

                    }
                    items.add(new LendTM(borrowerID, isbn, today, value + ""));

                    txtIsbn.clear();
                    txtBookName.clear();
                    txtBorrowID.setDisable(true);
                    txtBorrowerName.setDisable(true);
                    btnSave.setDisable(false);
                    tblBorrowerDetail.setDisable(true);
                }else{
                    new Alert(Alert.AlertType.ERROR,"Please select Date").showAndWait();
                    clndrReturn.requestFocus();
                }

            }
        }else{
            new Alert(Alert.AlertType.INFORMATION,"Only two books can borrow in one time").showAndWait();
            btnAddToTable.setDisable(true);
            btnSave.requestFocus();
        }

    }

    public void imgHomeOnClicked(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Console.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) this.root.getScene().getWindow();
        stage.setScene(scene);
        Image imge = new Image("/image/icons8-books-48.png");
        stage.getIcons().add(imge);
        stage.setTitle("Console");
        stage.centerOnScreen();
    }
}
