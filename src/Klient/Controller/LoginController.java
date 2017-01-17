package Klient.Controller;

import Klient.MainApp;
import Klient.Model.Client;
import Klient.Model.RSA;
import Klient.Model.ServerElement;
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
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static Server.Model.FileIO.loadFile;
import static Server.Model.FileIO.saveFile;

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
    private String settings;
    private ArrayList<ServerElement> serverList;
    private RSA rsa;

    public void setMainApp(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                saveSettings();
                System.exit(0);
            }
        });
        ini();
        sdf = new SimpleDateFormat("HH:mm:ss");
        rsa = new RSA(1024);
        loadSettings();
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

        Client client = new Client(IP, port, Login, TreeClient, rsa);
        if (client.start()) {
            connectOK(e, client);
        } else {
            printLog("Laczenie zakonczone niepowodzeniem");
            client.disconnect();
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
            stage.setResizable(false);
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

        ButtonList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openWindowServerList(event);
            }
        });

        this.ClientLog.setWrapText(true);
        //this.ClientLog.setDisable(true);
    }

    private void openWindowServerList(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("View/ServerListWindow.fxml"));
            Pane serverListWindow = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(serverListWindow));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent event) {
                    ((Stage) ((Node) e.getSource()).getScene().getWindow()).show();
                }
            });
            stage.setTitle("Lista zapamietanych serwerow");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setResizable(false);
            stage.show();

            ((Node) e.getSource()).getScene().getWindow().hide();

            ServerListWindowController controller = loader.getController();
            if (serverList.isEmpty())
                serverList.add(new ServerElement("Obecny serwer", TextIP.getText(), TextPort.getText()));
            controller.iniWindow(serverList, e, this);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void loadSettings() {
        serverList = new ArrayList<>();
        File config = new File("./client.conf");

        String defaultSettings = "#Server: 127.0.0.1\n" +
                "#Port: 1500\n" +
                "#Username: Grzechu\n" +
                "#END\n";

        if (config.exists()) {
            settings = loadFile(config);
        } else settings = defaultSettings;

        if (settings.isEmpty())
            settings = defaultSettings;

        String line = getLine();
        if (line.length() > 7 && line.substring(0, 7).equals("#Server")) {
            this.TextIP.setText(line.substring(9));
            line = getLine();
        }
        if (line.length() > 5 && line.substring(0, 5).equals("#Port")) {
            this.TextPort.setText(line.substring(7));
            line = getLine();
        }
        if (line.length() > 9 && line.substring(0, 9).equals("#Username")) {
            this.TextLogin.setText(line.substring(11));
            line = getLine();
        }
        if (line.length() > 4 && line.substring(0, 4).equals("#RSA")) {
            BigInteger n, d, e;
            line = getLine();
            n = new BigInteger(line);
            line = getLine();
            d = new BigInteger(line);
            line = getLine();
            e = new BigInteger(line);
            this.rsa = new RSA(n, d, e);
            line = getLine();
        }

        if (line.length() > 11 && line.substring(0, 11).equals("#ServerList")) {
            line = getLine();
            while (!line.equals("#END")) {
                int index = line.indexOf(';');
                String name = line.substring(0, index);
                line = line.substring(index + 1);
                index = line.indexOf(';');
                String IP = line.substring(0, index);
                line = line.substring(index + 1);
                String port = line;
                serverList.add(new ServerElement(name, IP, port));
                line = getLine();
            }
        }
    }

    private String getLine() {
        String line = "";
        int i = 0;
        char symbol = settings.charAt(i);
        while (symbol != '\n' && i < settings.length()) {
            line += symbol;
            symbol = settings.charAt(++i);
        }
        settings = settings.substring(i + 1);
        return line;
    }

    public void setServer(ServerElement selectedItem) {
        this.TextIP.setText(selectedItem.getIP());
        this.TextPort.setText(selectedItem.getPort());
    }

    private void saveSettings() {
        ArrayList<String> dataToSave = new ArrayList<>();
        String dataLine = this.TextIP.getText();
        if (!dataLine.isEmpty())
            dataToSave.add("#Server: " + dataLine);
        dataLine = this.TextPort.getText();
        if (!dataLine.isEmpty())
            dataToSave.add("#Port: " + dataLine);
        dataLine = this.TextLogin.getText();
        if (!dataLine.isEmpty())
            dataToSave.add("#Username: " + dataLine);
        dataToSave.add("#RSA:");
        dataToSave.add(rsa.getN().toString());
        dataToSave.add(rsa.getD().toString());
        dataToSave.add(rsa.getE().toString());
        dataToSave.add("#ServerList:");
        for (ServerElement aServerList : serverList) {
            dataToSave.add(aServerList.toString());
        }
        dataToSave.add("#END");
        dataToSave.add("");

        File config = new File("./client.conf");
        saveFile(config.getPath(), dataToSave);
    }

}
