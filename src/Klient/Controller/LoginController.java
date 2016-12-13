package Klient.Controller;

import Klient.MainApp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private MainApp mainApp;
    @FXML
    private Label LabelStatus;
    @FXML
    private Button ButtonConnect;
    @FXML
    private Button ButtonList;
    @FXML
    private TextField TextIP;
    @FXML
    private TextField TextLogin;


    public void setMainApp(MainApp mainApp) {
        ini();
        this.mainApp = mainApp;
    }

    private void connect(String IP, ActionEvent e){
        boolean connected = false;
        LabelStatus.setText("Łączenie z " + IP);

        if (!checkIP(IP)) LabelStatus.setText("Łączenie z " + IP + "BAD");

        connected = true;

        if (connected == true){
            connectOK(e);
        }
        else;

    }

    private boolean checkIP(String IP) {
    return true;
    }

    private void connectOK(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("View/Komunikator.fxml"));
            Pane komunikator = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(komunikator));
            stage.setTitle("Komunikator");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

            ((Node) e.getSource()).getScene().getWindow().hide();

            KomunikatorController controller = loader.getController();
            controller.iniWindow();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private void ini(){
        ButtonConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connect(TextIP.getText(), event);
            }
        });
    }
}
