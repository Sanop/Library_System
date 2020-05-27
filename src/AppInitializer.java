import db.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AppInitializer extends Application {

    public static void main(String[] args) {

        launch(args);
        try {
            DBConnection.getInstance().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        Image img = new Image("/image/icons8-books-48.png");
        primaryStage.getIcons().add(img);
        primaryStage.setTitle("Login");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
