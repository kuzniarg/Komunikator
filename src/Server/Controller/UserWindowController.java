package Server.Controller;

import Klient.Model.ChatMessage;
import Server.Model.Server;
import Server.Model.UserKey;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class UserWindowController {
    @FXML
    private Button buttonOK;
    @FXML
    private Button buttonAbort;
    @FXML
    private Label labelLogin;
    @FXML
    private Label labelCanal;
    @FXML
    private Slider sliderPower;
    @FXML
    private CheckBox checkBoxBaza;
    @FXML
    private Button buttonKick;
    @FXML
    private Button buttonBan;

    private ArrayList<UserKey> userList;
    private UserKey user;
    private int port;
    private Server server;

    public void iniWindow(ArrayList<UserKey> userList, UserKey user, String canalName, int port, Server server) {
        this.server = server;
        this.userList = userList;
        this.port = port;
        this.user = user;
        this.labelLogin.setText(user.getName());
        this.labelCanal.setText(String.valueOf(canalName));
        this.sliderPower.setValue(Integer.parseInt(user.getPower()));
        this.checkBoxBaza.setSelected(isInBase());
        buttonOK.setDisable(!checkBoxBaza.isSelected());
        ini();
    }

    private boolean isInBase() {
        for (UserKey uk : userList) {
            if (user.equals(uk))
                return true;
        }
        return false;
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
                server.changeUserPower(user, (int) sliderPower.getValue(), labelCanal.getText());
                ((Node) e.getSource()).getScene().getWindow().hide();
            }
        });

        buttonKick.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                server.kickUser(user, labelCanal.getText());
                refreshServer();
                ((Node) e.getSource()).getScene().getWindow().hide();
            }
        });

        buttonBan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                server.changeUserPower(user, -1, labelCanal.getText());
                server.kickUser(user, labelCanal.getText());
                refreshServer();
                ((Node) e.getSource()).getScene().getWindow().hide();
            }
        });

        checkBoxBaza.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (checkBoxBaza.isSelected())
                    buttonOK.setDisable(false);
                else buttonOK.setDisable(true);
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
