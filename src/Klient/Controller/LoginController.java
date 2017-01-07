package Klient.Controller;

import Klient.MainApp;
import Klient.Model.Client;
import Server.Model.CustomOutputStream;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginController {
    @FXML
    private TextArea ClientLog;
    @FXML
    private Button ButtonConnect;
    @FXML
    private Button ButtonList;
    @FXML
    private TextField TextIP;
    @FXML
    private TextField TextPort;
    @FXML
    private TextField TextLogin;
    private SimpleDateFormat sdf;


    public void setMainApp() {
        ini();
        sdf = new SimpleDateFormat("HH:mm:ss");
        PrintStream printStream = new PrintStream(new CustomOutputStream(ClientLog));
        System.setOut(printStream);
        //System.setErr(printStream);
    }

    private void connect(ActionEvent e) {
        String IP = TextIP.getText();
        String portText = TextPort.getText();
        int port;
        if (portText.compareTo("") != 0)
            port = Integer.parseInt(portText);
        else {
            printLog("Prosze podac numer portu");
            return;
        }
        String Login = TextLogin.getText();
        if (Login.compareTo("") == 0) {
            printLog("Prosze podac login");
            return;
        }

        printLog("Laczenie z " + IP);

        TreeView<String> TreeClient = new TreeView<>();

        Client client = new Client(IP, port, Login, TreeClient);
        if (client.start()) {
            connectOK(e, client);
        }
    }

    private void printLog(String msg) {
        String time = sdf.format(new Date()) + " " + msg + "\n";
        System.out.print(time);
    }

    private void connectOK(ActionEvent e, Client client) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("View/Komunikator.fxml"));
            Pane komunikator = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(komunikator));
            stage.setTitle("Komunikator (" + TextIP.getText() + ") " + TextLogin.getText());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

            ((Node) e.getSource()).getScene().getWindow().hide();

            KomunikatorController controller = loader.getController();
            controller.iniWindow(client, stage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private void ini() {
        ButtonConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connect(event);
            }
        });
        this.ClientLog.setWrapText(true);
        this.ClientLog.setDisable(true);
    }
}
