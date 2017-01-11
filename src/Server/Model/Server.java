package Server.Model;

import javafx.scene.control.TreeView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server implements Runnable {
    private int port;
    private TreeView<String> TreeServer;
    private ArrayList<Canal> canalList;
    private ArrayList<ClientThread> clientList;
    private ArrayList<UserKey> userList;
    private SimpleDateFormat sdf;
    private boolean keepGoing;
    private int uniqueId;

    public Server(int port, TreeView<String> TreeServer, ArrayList<Canal> canalList, ArrayList<UserKey> userList) {
        this.port = port;
        this.TreeServer = TreeServer;
        this.canalList = canalList;
        this.userList = userList;
        sdf = new SimpleDateFormat("HH:mm:ss");
        clientList = new ArrayList<ClientThread>();
    }

    public void run() {
        keepGoing = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            display("Serwer oczekuje na polaczenia - port " + port);
            while (keepGoing) {
                Socket socket = serverSocket.accept();

                if (!keepGoing)
                    break;

                ClientThread t = new ClientThread(socket, String.valueOf(++uniqueId), clientList, TreeServer, canalList);
                String publicKey = t.getPublicKey();
                int power = isUserRegistered(publicKey);
                if (power > -1) {
                    t.setClientPower(power);
                    clientList.add(t);
                    t.start();
                }
            }

            try {
                serverSocket.close();
                for (ClientThread tempThread : clientList) {
                    try {
                        tempThread.sInput.close();
                        tempThread.sOutput.close();
                        tempThread.socket.close();
                    } catch (IOException ignored) {
                    }
                }
            } catch (Exception e) {
                display("Blad podczas zamykania serwera: " + e);
            }
        } catch (IOException e) {
            String msg = "Blad podczas tworzenia nowego ServerSocket: " + e;
            display(msg);
        }
    }

    private int isUserRegistered(String publicKey) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getPublicKey().equals(publicKey)) {
                return userList.get(i).getPower();
            }
        }
        return 0;
    }

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg + "\n";
        System.out.print(time);
    }

    public void stopServer() {
        keepGoing = false;
        try {
            new Socket("localhost", port);
        } catch (Exception ignored) {
        }
    }
}
