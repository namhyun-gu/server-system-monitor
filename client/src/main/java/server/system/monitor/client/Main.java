package server.system.monitor.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getResource("/server.system.monitor/MainLayout.fxml");
        Parent root = FXMLLoader.load(url);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Server System Monitor");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
