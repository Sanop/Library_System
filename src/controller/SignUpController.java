package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {
    public JFXTextField txtUserName;
    public JFXTextField txtEmailAddress;
    public JFXPasswordField txtNewPassword;
    public JFXPasswordField txtConfirmPassword;
    public JFXButton btnSignUp;
    public JFXCheckBox checkBoxAccept;
    public AnchorPane root;
    public Label lblMatch;
    public Label lblMatch1;

    public void initialize(){
        FadeTransition ft = new FadeTransition();
        ft.setNode(root);
        ft.setDuration(Duration.millis(2000));
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public void btnSignUpOnAction(ActionEvent event) {
        if(txtUserName.getText().trim().isEmpty()){
            txtUserName.requestFocus();
        }else if(txtEmailAddress.getText().trim().isEmpty()){
            txtEmailAddress.requestFocus();
        }else if(txtNewPassword.getText().trim().isEmpty()){
            txtNewPassword.requestFocus();
        }else if(txtConfirmPassword.getText().trim().isEmpty()){
            txtConfirmPassword.requestFocus();
        }else if(checkBoxAccept.isSelected()){
            String password = txtNewPassword.getText();
            String confirmPassword = txtConfirmPassword.getText();
            if(password.trim().equals(confirmPassword.trim())){
                lblMatch.setVisible(false);
                lblMatch1.setVisible(true);
                new Alert(Alert.AlertType.CONFIRMATION,"Success").showAndWait();
                signUp();
            }else{
                lblMatch.setVisible(true);
            }
        }else{
            checkBoxAccept.requestFocus();
        }
    }

    public void signUp(){
        String userName = txtUserName.getText();
        String email = txtEmailAddress.getText();
        String password = txtConfirmPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into users values(?,?,?)");
            preparedStatement.setObject(1,userName);
            preparedStatement.setObject(2,email);
            preparedStatement.setObject(3,password);
            int i = preparedStatement.executeUpdate();
            if(i!=0){
                Image img = new Image("image/icons8-checked-radio-button-48.png");
                Notifications notifications = Notifications.create()
                        .title("Confirm")
                        .text("New User Added")
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(2))
                        .position(Pos.BOTTOM_RIGHT);
                notifications.show();
                this.root.getScene().getWindow().hide();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
