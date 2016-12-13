package Server;

import Server.Controller.StartServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerMain extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        initWindowStartServer();
    }

    private void initWindowStartServer() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ServerMain.class.getResource("View/StartServer.fxml"));
            Pane windowLogin = loader.load();

            Scene scene = new Scene(windowLogin);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Server");
            primaryStage.setResizable(false);
            primaryStage.show();

            StartServerController controller = loader.getController();
            controller.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
