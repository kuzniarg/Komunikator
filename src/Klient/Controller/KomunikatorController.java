package Klient.Controller;

import Klient.Model.ChatMessage;
import Klient.Model.Client;
import Server.Model.CustomOutputStream;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.PrintStream;

public class KomunikatorController {
    private Client client;
    @FXML
    private TextArea ChatArea;
    @FXML
    private TextField TextMsg;
    @FXML
    private Button ButtonSend;
    @FXML
    private TreeView<String> TreeClient;

    public void iniWindow(Client client, Stage stage) {
        this.client = client;
        ini(stage);
        PrintStream printStream = new PrintStream(new CustomOutputStream(ChatArea));
        System.setOut(printStream);
        //System.setErr(printStream);
        client.setTreeClient(TreeClient);
    }

    private void ini(Stage stage) {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                client.disconnect();
                System.exit(0);
            }
        });

        ButtonSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendMsg();
            }
        });

        TextMsg.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    sendMsg();
                }
            }
        });

        this.ChatArea.setWrapText(true);
        //this.ChatArea.setDisable(true);

        TreeClient.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    TreeItem<String> item = TreeClient.getSelectionModel().getSelectedItem();
                    client.sendMessage(new ChatMessage(ChatMessage.CHANGE, item.getValue()));
                }
            }
        });

    }

    private void sendMsg() {
        client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, TextMsg.getText()));
        TextMsg.setText("");
    }
}
