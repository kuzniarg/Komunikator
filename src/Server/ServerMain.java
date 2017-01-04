package Server;

import Server.Controller.ServerWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerMain extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        initWindowStartServer();
    }

    private void initWindowStartServer() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ServerMain.class.getResource("View/ServerWindow.fxml"));
            Pane windowLogin = loader.load();

            Scene scene = new Scene(windowLogin);
            stage.setScene(scene);
            stage.setTitle("Server");
            stage.setResizable(false);
            stage.show();

            ServerWindowController controller = loader.getController();
            controller.run(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
