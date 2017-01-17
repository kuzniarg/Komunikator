package Server.Controller;

import Klient.Model.ChatMessage;
import Server.Model.Canal;
import Server.Model.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class EditCanalWindowController {
    @FXML
    private Button buttonOK;
    @FXML
    private Button buttonAbort;
    @FXML
    private TextField textName;
    @FXML
    private Slider sliderPower;
    @FXML
    private Button buttonUsun;
    @FXML
    private Button buttonGora;
    @FXML
    private Button buttonDol;

    private TreeView<String> TreeServer;
    private ArrayList<Canal> canalList;
    private int canalNumber;
    private int port;

    public void iniWindow(ArrayList<Canal> canalList, TreeView<String> TreeServer, int canalNumber, int port) {
        ini();
        this.canalList = canalList;
        this.TreeServer = TreeServer;
        this.canalNumber = canalNumber;
        this.port = port;
        this.textName.setText(canalList.get(canalNumber).getName());
        this.sliderPower.setValue(canalList.get(canalNumber).getPower());
    }

    private void ini() {
        buttonAbort.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                ((Node) e.getSource()).getScene().getWindow().hide();
            }
        });

        buttonOK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!textName.getText().equals("")) {
                    Canal newCanal = new Canal(textName.getText(), (int) sliderPower.getValue());
                    ArrayList<User> tmp = canalList.get(canalNumber).getUsers();
                    canalList.set(canalNumber, newCanal);
                    canalList.get(canalNumber).setUsers(tmp);
                    TreeServer.getRoot().getChildren().get(canalNumber).setValue(textName.getText());
                    refreshServer();
                    ((Node) e.getSource()).getScene().getWindow().hide();
                }
            }
        });

        buttonUsun.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                canalList.remove(canalNumber);
                TreeServer.getRoot().getChildren().remove(canalNumber);
                refreshServer();
                ((Node) e.getSource()).getScene().getWindow().hide();
            }
        });

        buttonGora.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (canalNumber < 2)
                    return;
                Collections.swap(canalList, canalNumber, canalNumber - 1);
                Collections.swap(TreeServer.getRoot().getChildren(), canalNumber, canalNumber - 1);
                canalNumber--;
                refreshServer();
            }
        });

        buttonDol.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (canalNumber >= canalList.size() - 1)
                    return;
                Collections.swap(canalList, canalNumber, canalNumber + 1);
                Collections.swap(TreeServer.getRoot().getChildren(), canalNumber, canalNumber + 1);
                canalNumber++;
                refreshServer();
            }
        });
    }

    private void refreshServer() {
        try {
            Socket socket = new Socket("localhost", port);
            ObjectOutputStream sOutput = new ObjectOutputStream(socket.getOutputStream());
            sOutput.writeObject("serwer");
            sOutput.writeObject(new ChatMessage(ChatMessage.AUTHORIZATION, "serverRefresh"));
            sOutput.close();
            socket.close();
        } catch (Exception ignored) {
        }
    }
}
