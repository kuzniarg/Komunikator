package Klient.Controller;

import Klient.MainApp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.concurrent.TimeUnit;

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

    private void connect(String IP){
        LabelStatus.setText("Łączenie z " + IP);
    }


    private void ini(){
        ButtonConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connect(TextIP.getText());
            }
        });
    }
}
