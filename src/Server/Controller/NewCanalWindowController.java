package Server.Controller;

import Klient.Model.ChatMessage;
import Server.Model.Canal;
import Server.Model.Server;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class NewCanalWindowController {
    @FXML
    private Button buttonOK;
    @FXML
    private Button buttonAbort;
    @FXML
    private TextField textName;
    @FXML
    private Slider sliderPower;

    private TreeView<String> TreeServer;
    private ArrayList<Canal> canalList;
    private Server server;
    private int port;

    public void iniWindow(ArrayList<Canal> canalList, TreeView<String> TreeServer, int port) {
        ini();
        this.canalList = canalList;
        this.TreeServer = TreeServer;
        this.port = port;
    }

    private void ini() {
        buttonOK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!textName.getText().equals("")) {
                    Canal newCanal = new Canal(textName.getText(), (int) sliderPower.getValue());
                    canalList.add(newCanal);
                    TreeServer.getRoot().getChildren().add(new TreeItem<>(newCanal.getName()));
                    TreeServer.getRoot().getChildren().get(canalList.size() - 1).setExpanded(true);
                    try {
                        Socket socket = new Socket("localhost", port);
                        ObjectOutputStream sOutput = new ObjectOutputStream(socket.getOutputStream());
                        sOutput.writeObject("serwer");
                        sOutput.writeObject(new ChatMessage(ChatMessage.AUTHORIZATION, "serverRefresh"));
                        sOutput.close();
                        socket.close();
                    } catch (Exception ignored) {
                    }
                    ((Node) e.getSource()).getScene().getWindow().hide();
                }
            }
        });

        buttonAbort.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                ((Node) e.getSource()).getScene().getWindow().hide();
            }
        });
    }
}
