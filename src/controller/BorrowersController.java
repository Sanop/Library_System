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
import javafx.scene.control.ButtonType;
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
import util.BorrowerTM;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class BorrowersController {

    public JFXTextField txtBorrower_Name;
    public JFXTextField txtBorrower_Address;
    public JFXTextField txtBorrower_NIC;
    public JFXTextField txtBorrower_Contact;
    public JFXButton btnAddNew;
    public JFXButton btnDelete;
    public JFXButton btnSave;
    public Label lblID;
    public TableView<BorrowerTM> tblBorrowers;
    public AnchorPane root;

    public void initialize(){
        TranslateTransition tt = new TranslateTransition();
        tt.setDuration(Duration.millis(500));
        tt.setNode(root);
        tt.setFromX(-10);
        tt.setToX(0);
        tt.play();

        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        btnAddNew.requestFocus();
        txtBorrower_Address.setDisable(true);
        txtBorrower_Contact.setDisable(true);
        txtBorrower_Name.setDisable(true);
        txtBorrower_NIC.setDisable(true);

        tblBorrowers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("borrower_ID"));
        tblBorrowers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("borrower_Name"));
        tblBorrowers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("borrower_Address"));
        tblBorrowers.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("borrower_NIC"));
        tblBorrowers.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("borrower_Contact"));

        loadTable();

        tblBorrowers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BorrowerTM>() {
            @Override
            public void changed(ObservableValue<? extends BorrowerTM> observable, BorrowerTM oldValue, BorrowerTM newValue) {
                int selectedIndex = tblBorrowers.getSelectionModel().getSelectedIndex();
                if(selectedIndex == -1){
                    return;
                }

                BorrowerTM selectedItem = tblBorrowers.getSelectionModel().getSelectedItem();

                txtBorrower_Name.setText(selectedItem.getBorrower_Name());
                txtBorrower_Contact.setText(selectedItem.getBorrower_Contact());
                txtBorrower_NIC.setText(selectedItem.getBorrower_NIC());
                txtBorrower_Address.setText(selectedItem.getBorrower_Address());
                lblID.setText(selectedItem.getBorrower_ID());

                txtBorrower_Name.setDisable(false);
                txtBorrower_Contact.setDisable(false);
                txtBorrower_NIC.setDisable(false);
                txtBorrower_Address.setDisable(false);

                btnSave.setText("Update");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
    }

    public void btnAddNewOnAction(ActionEvent event) {
        autoGenerateID();
        txtBorrower_Address.setDisable(false);
        txtBorrower_Contact.setDisable(false);
        txtBorrower_Name.setDisable(false);
        txtBorrower_NIC.setDisable(false);

        btnSave.setDisable(false);
        btnSave.setText("Save");
        txtBorrower_Name.requestFocus();
    }

    public void btnDeleteOnAction(ActionEvent event) {

        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to delete this borrower", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get() == ButtonType.YES){
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from borrowers where borrower_ID = ?");
                preparedStatement.setObject(1,lblID.getText());
                preparedStatement.executeUpdate();

                txtBorrower_Name.clear();
                txtBorrower_Contact.clear();
                txtBorrower_NIC.clear();
                txtBorrower_Address.clear();
                lblID.setText("");
                btnAddNew.requestFocus();
                btnSave.setDisable(true);
                btnDelete.setDisable(true);


                txtBorrower_Address.setDisable(true);
                txtBorrower_Contact.setDisable(true);
                txtBorrower_Name.setDisable(true);
                txtBorrower_NIC.setDisable(true);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadTable();
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
        if(txtBorrower_Name.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields Can Not Be Empty").showAndWait();
            txtBorrower_Name.requestFocus();
        }else if(txtBorrower_Address.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields Can Not Be Empty").showAndWait();
            txtBorrower_Address.requestFocus();
        }else if(txtBorrower_NIC.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields Can Not Be Empty").showAndWait();
            txtBorrower_NIC.requestFocus();
        }else if (txtBorrower_Contact.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Fields Can Not Be Empty").showAndWait();
            txtBorrower_Contact.requestFocus();
        }else{
            if(txtBorrower_NIC.getText().matches("\\d{9}[vV]")){
                if(txtBorrower_Contact.getText().matches("\\d{3}[-]\\d{7}")){
                    if(btnSave.getText().equals("Save")){
                        save();
                    }else{
                        update();
                    }

                }else{
                    new Alert(Alert.AlertType.ERROR,"Please Enter Contact in Correct Format\nEg:-(XXX-XXXXXXX)").showAndWait();
                    txtBorrower_Contact.requestFocus();
                }
            }else{
                new Alert(Alert.AlertType.ERROR,"Please Enter NIC in Correct Format\nEg:-(123456789V)").showAndWait();
                txtBorrower_NIC.requestFocus();
            }

        }

    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select borrower_ID from borrowers order by borrower_ID desc limit 1");
            if(resultSet.next()){
                String lastIDString = resultSet.getString(1);
                String lastNumberString = lastIDString.substring(2, 5);
                int lastNumberInt = Integer.parseInt(lastNumberString);
                int newNumberInt = lastNumberInt + 1;
                if(newNumberInt < 10){
                    lblID.setText("BW00"+newNumberInt);
                }else if(newNumberInt < 100){
                    lblID.setText("BW0"+newNumberInt);
                }else {
                    lblID.setText("BW"+newNumberInt);
                }
            }else {
                lblID.setText("BW001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        txtBorrower_Name.clear();
        txtBorrower_Contact.clear();
        txtBorrower_NIC.clear();
        txtBorrower_Address.clear();
        btnDelete.setDisable(true);
    }

    public void save(){

        String id = lblID.getText();
        String name = txtBorrower_Name.getText();
        String address = txtBorrower_Address.getText();
        String nic = txtBorrower_NIC.getText();
        String text = txtBorrower_Contact.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into borrowers values (?,?,?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,name);
            preparedStatement.setObject(3,address);
            preparedStatement.setObject(4,nic);
            preparedStatement.setObject(5,text);

            int i = preparedStatement.executeUpdate();
            if(i!= 0){
                Image img = new Image("image/icons8-checked-radio-button-48.png");
                Notifications notifications = Notifications.create()
                        .title("Confirm")
                        .text("Add to Database")
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.BOTTOM_RIGHT);
                notifications.show();
                loadTable();
                txtBorrower_Name.clear();
                txtBorrower_Contact.clear();
                txtBorrower_NIC.clear();
                txtBorrower_Address.clear();
                lblID.setText("");
                btnAddNew.requestFocus();
                btnSave.setDisable(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTable(){
        ObservableList<BorrowerTM> items = tblBorrowers.getItems();
        items.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from borrowers");
            while (resultSet.next()){
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String address = resultSet.getString(3);
                String nic = resultSet.getString(4);
                String contact = resultSet.getString(5);

                items.add(new BorrowerTM(id,name,address,nic,contact));
            }

            tblBorrowers.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(){
        String id = lblID.getText();
        String name = txtBorrower_Name.getText();
        String address = txtBorrower_Address.getText();
        String nic = txtBorrower_NIC.getText();
        String text = txtBorrower_Contact.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update borrowers set borrower_Name = ?,borrower_Address = ?,borrower_NIC = ?,borrower_Contact = ? where borrower_ID = ?");
            preparedStatement.setObject(1,name);
            preparedStatement.setObject(2,address);
            preparedStatement.setObject(3,nic);
            preparedStatement.setObject(4,text);
            preparedStatement.setObject(5,id);

            int i = preparedStatement.executeUpdate();
            if(i!=0){
                Image img = new Image("image/icons8-checked-radio-button-48.png");
                Notifications notifications = Notifications.create()
                        .title("Confirm")
                        .text("Update Database")
                        .hideAfter(Duration.seconds(3))
                        .graphic(new ImageView(img))
                        .position(Pos.BOTTOM_RIGHT);
                notifications.show();
                loadTable();
                txtBorrower_Name.clear();
                txtBorrower_Contact.clear();
                txtBorrower_NIC.clear();
                txtBorrower_Address.clear();
                lblID.setText("");
                btnAddNew.requestFocus();
                btnSave.setDisable(true);
                btnDelete.setDisable(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
