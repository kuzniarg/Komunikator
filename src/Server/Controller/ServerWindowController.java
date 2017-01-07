package Server.Controller;

import Server.Model.CustomOutputStream;
import Server.Model.Server;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerWindowController {
    @FXML
    private Button ButtonStart;
    @FXML
    private TextField TextIP;
    @FXML
    private TextField TextPort;
    @FXML
    private TextArea ServerLog;
    @FXML
    private TreeView<String> TreeServer;

    private SimpleDateFormat sdf;
    private boolean startClick = true;
    private Server server;
    private Thread serverThread;

    public void run(Stage stage) {
        ini(stage);
        printIP();
        sdf = new SimpleDateFormat("HH:mm:ss");
        PrintStream printStream = new PrintStream(new CustomOutputStream(ServerLog));
        System.setOut(printStream);
        //System.setErr(printStream);
        printLog("---Aplikacja serwer gotowa do uzytku---");
    }

    private void printIP() {
        TextIP.setText("xxx.xxx.xx.xxx");
    }

    public void printLog(String msg) {
        String time = sdf.format(new Date()) + " " + msg + "\n";
        System.out.print(time);
    }

    private void start(String Port) {
        if (startClick) {
            int port;

            try {
                port = Integer.parseInt(Port);
            } catch (Exception e1) {
                printLog("Zły format portu");
                return;
            }
            server = new Server(port, TreeServer);
            serverThread = new Thread(server);
            serverThread.start();
            printLog("Start serwera");

            ButtonStart.setText("Stop");
            startClick = false;
        } else {
            ButtonStart.setText("Start");
            startClick = true;

            server.stopServer();
            printLog("Zatrzymanie serwera");
        }

    }

    private void ini(Stage stage) {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                if (server != null)
                    server.stopServer();
                System.exit(0);
            }
        });

        ButtonStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                start(TextPort.getText());
            }
        });

        this.ServerLog.setWrapText(true);
        loadCanals();
    }

    private void loadCanals() {
        TreeItem<String> rootItem = new TreeItem<>("Serwer");
        rootItem.setExpanded(true);
        rootItem.getChildren().add(new TreeItem<>("Poczekalnia"));
        rootItem.getChildren().get(0).setExpanded(true);
        for (int i = 2; i < 8; i++) {
            rootItem.getChildren().add(new TreeItem<>("Kanal " + i));
            rootItem.getChildren().get(i - 1).setExpanded(true);
        }
        this.TreeServer.setRoot(rootItem);
    }
}
