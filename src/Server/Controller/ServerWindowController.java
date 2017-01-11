package Server.Controller;

import Server.Model.Canal;
import Server.Model.CustomOutputStream;
import Server.Model.Server;
import Server.Model.UserKey;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static Server.Model.FileIO.loadFile;
import static Server.Model.FileIO.saveFile;

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
    @FXML
    private Menu menuUstawienia;
    @FXML
    private Menu menuWidok;
    @FXML
    private Menu menuPomoc;

    private SimpleDateFormat sdf;
    private boolean startClick = true;
    private Server server;
    private ArrayList<Canal> canalList;
    private ArrayList<UserKey> userList;
    private String settings;

    public void run(Stage stage) {
        ini(stage);
        printIP();
        sdf = new SimpleDateFormat("HH:mm:ss");
        PrintStream printStream = new PrintStream(new CustomOutputStream(ServerLog));
        System.setOut(printStream);
        //System.setErr(printStream);
        loadSettings();
        printLog("---Aplikacja serwer gotowa do uzytku---");
    }

    private void printIP() {
        TextIP.setText("xxx.xxx.xx.xxx");
    }

    private void printLog(String msg) {
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
            server = new Server(port, TreeServer, canalList, userList);
            Thread serverThread = new Thread(server);
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
                saveSettings();
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
    }

    private void loadSettings() {
        File config = new File("./server.conf");

        String defaultSettings = "#Canals\n" +
                "Poczekalnia;0\n" +
                "Administracja;9\n" +
                "Towarzyski;0\n" +
                "Prywatny 1;3\n" +
                "Prywatny 2;5\n" +
                "Prywatny 3;7\n" +
                "###\n" +
                "#Users\n" +
                "###\n" +
                "#END";

        if (config.exists()) {
            settings = loadFile(config);
        } else settings = defaultSettings;

        String section = getNextSection();
        if (!section.equals(""))
            loadCanals(section);
        section = getNextSection();
        if (!section.equals(""))
            loadUsers(section);
    }

    private String getNextSection() {
        String section = "", line;
        line = getLine();
        if (line.charAt(0) == '#')
            line = getLine();
        while (line.charAt(0) != '#' && settings.length() != 0) {
            section += line + "\n";
            line = getLine();
        }
        return section;
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

    private void loadCanals(String section) {
        canalList = new ArrayList<>();
        char symbol = section.charAt(0);
        while (symbol != '\n') {
            int index = section.indexOf(';');
            String name = section.substring(0, index);
            int index2 = section.indexOf('\n');
            int power = Integer.parseInt(section.substring(index + 1, index2));
            canalList.add(new Canal(name, power));
            section = section.substring(index2 + 1);
            if (index2 >= section.length()) break;
        }
        crateTree();
        printLog("Wczytano baze kanalow");
    }

    private void crateTree() {
        TreeItem<String> rootItem = new TreeItem<>("Serwer");
        rootItem.setExpanded(true);
        for (int i = 0; i < canalList.size(); i++) {
            rootItem.getChildren().add(new TreeItem<>(canalList.get(i).getName()));
            rootItem.getChildren().get(i).setExpanded(true);
        }
        this.TreeServer.setRoot(rootItem);
    }

    private void loadUsers(String section) {
        userList = new ArrayList<>();
        char symbol = section.charAt(0);
        while (symbol != '\n') {
            int index = section.indexOf(';');
            String publicKey = section.substring(0, index);
            int index2 = section.indexOf('\n');
            int power = Integer.parseInt(section.substring(index + 1, index2));
            userList.add(new UserKey(publicKey, power));
            section = section.substring(index2 + 1);
            if (index2 >= section.length()) break;
        }
        printLog("Wczytano baze uzytkownikow");
    }

    private void saveSettings() {
        ArrayList<String> dataToSave = new ArrayList<>();
        dataToSave.add("#Canals");
        for (Canal aCanalList : canalList) {
            dataToSave.add(aCanalList.toStringWithoutUsers());
        }
        dataToSave.add("###");
        dataToSave.add("#Users");
        if (userList != null)
            for (UserKey anUserList : userList) {
                dataToSave.add(anUserList.toString());
            }
        dataToSave.add("###");
        dataToSave.add("#END");

        File config = new File("./server.conf");
        saveFile(config.getPath(), dataToSave);
    }

}
