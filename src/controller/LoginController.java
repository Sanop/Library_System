package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    public JFXTextField txtUserName;
    public JFXPasswordField txtPassword;
    public JFXButton btnLogin;
    public JFXButton btnSignUp;
    public AnchorPane root;

    public void initialize(){
        FadeTransition ft = new FadeTransition();
        ft.setNode(root);
        ft.setDuration(Duration.millis(2000));
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public void btnLoginOnAction(ActionEvent event) {
        if(txtUserName.getText().trim().isEmpty()){
            txtUserName.requestFocus();
        }else if(txtPassword.getText().trim().isEmpty()){
            txtPassword.requestFocus();
        }else{
            login();
        }

    }

    public void btnSignUpOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/SignUp.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        Image imge = new Image("/image/icons8-books-48.png");
        stage.getIcons().add(imge);
        stage.setTitle("Sign_Up Form");
        stage.centerOnScreen();
        stage.show();
    }

    public void txtPasswordOnAction(ActionEvent event) {
        login();
    }

    public void login(){
        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select user_Name,password from users where user_Name = ? and password = ?");
            preparedStatement.setObject(1,userName);
            preparedStatement.setObject(2,password);
            ResultSet i = preparedStatement.executeQuery();
            if(i.next()){
                try {
                    FXMLLoader fxmlLoader =  new FXMLLoader(this.getClass().getResource("/view/Console.fxml"));
                    Parent root = fxmlLoader.load();
                    ConsoleController controller = (ConsoleController) fxmlLoader.getController();

                    controller.setUser(userName);

                    Scene scene = new Scene(root);
                    Stage stage = (Stage)this.root.getScene().getWindow();
                    stage.setScene(scene);
                    Image imge = new Image("/image/icons8-books-48.png");
                    stage.getIcons().add(imge);
                    stage.setTitle("Console");
                    stage.centerOnScreen();

                    Image img = new Image("image/editor-1s-48px.png");
                    Notifications notifications = Notifications.create()
                            .title("Hello")
                            .text("Welcome to Library system")
                            .graphic(new ImageView(img))
                            .hideAfter(Duration.seconds(3))
                            .position(Pos.TOP_RIGHT);
                    notifications.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                new Alert(Alert.AlertType.ERROR,"Invalid UserName or Password").showAndWait();
                txtUserName.requestFocus();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
