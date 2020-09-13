package controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

import java.io.*;
import java.util.Optional;

public class ConsoleController {
    public AnchorPane root;
    public JFXButton btnLogout;
    public Label lblUserName;
    private static String userName;

    public void initialize(){
        FadeTransition ft = new FadeTransition();
        ft.setNode(root);
        ft.setDuration(Duration.millis(2000));
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        lblUserName.setText(userName);


    }

    public void btnAddNewBorrowerOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Borrowers.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage)this.root.getScene().getWindow();
        stage.setScene(scene);
        setIcon(stage);
        stage.setTitle("Borrower_Form");
        stage.centerOnScreen();
    }

    public void btnAddNewBookOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Books.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage)this.root.getScene().getWindow();
        stage.setScene(scene);
        setIcon(stage);
        stage.setTitle("Books_Form");
        stage.centerOnScreen();
    }

    public void btnLendBooksOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Lend.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage)this.root.getScene().getWindow();
        stage.setScene(scene);
        setIcon(stage);
        stage.setTitle("Lend_Form");
        stage.centerOnScreen();
    }

    public void btnReturnBooksOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Return.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage)this.root.getScene().getWindow();
        stage.setScene(scene);
        setIcon(stage);
        stage.setTitle("Return_Form");
        stage.centerOnScreen();
    }

    public void btnLogoutOnAction(ActionEvent event) throws IOException {


        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to logout..?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get().equals(ButtonType.YES)){
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage)this.root.getScene().getWindow();
            stage.setScene(scene);
            setIcon(stage);
            stage.centerOnScreen();
            stage.setTitle("Login");
        }
    }

    public void setUser(String userName){
        lblUserName.setText(userName);
        this.userName = userName;
    }

    public void setIcon(Stage stage){
        Image imge = new Image("/image/icons8-books-48.png");
        stage.getIcons().add(imge);
    }
}
