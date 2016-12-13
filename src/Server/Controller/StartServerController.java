package Server.Controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import Server.Model.Server;

public class StartServerController {
    @FXML
    private Button ButtonStart;
    @FXML
    private TextField TextIP;
    @FXML
    private TextField TextPort;
    @FXML
    private Label LabelStatus;

    public void run() {
        ini();
        printIP();
    }

    private void printIP() {
        TextIP.setText("xxx.xxx.xx.xxx");
    }

    private void start(String Port, ActionEvent e){
        // start server on port 1500 unless a PortNumber is specified
        int portNumber;
                try {
                    portNumber = Integer.parseInt(Port);
                }
                catch(Exception e1) {
                    LabelStatus.setText("Zły format portu");
                    return;
                }
        LabelStatus.setText("Inicjowanie serwera");
        Server server = new Server(portNumber);
        //server.start();
    }


    private void ini(){
        ButtonStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                start(TextPort.getText(), event);
            }
        });
    }
}
