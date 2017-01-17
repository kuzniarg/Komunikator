package Server.Model;

import Klient.Model.ChatMessage;
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
    private boolean printMsg;
    private boolean allowCanalChanging;

    public Server(int port, TreeView<String> TreeServer, ArrayList<Canal> canalList, ArrayList<UserKey> userList,
                  boolean printMsg, boolean allowCanalChanging) {
        this.port = port;
        this.TreeServer = TreeServer;
        this.canalList = canalList;
        this.userList = userList;
        this.printMsg = printMsg;
        this.allowCanalChanging = allowCanalChanging;
        sdf = new SimpleDateFormat("HH:mm:ss");
        clientList = new ArrayList<>();
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

                ClientThread t = new ClientThread(socket, String.valueOf(++uniqueId), clientList, TreeServer, canalList,
                        printMsg, allowCanalChanging);
                String publicKey = t.getPublicKey();
                int power;
                if (clientList != null)
                    power = isUserRegistered(publicKey, t);
                else power = 0;
                if (power > -1) {
                    t.setClientPower(power);
                    clientList.add(t);
                    t.start();
                } else
                    t.banned();

            }

            try {
                for (int i = clientList.size(); --i >= 0; ) {
                    ClientThread ct = clientList.get(i);
                    ct.writeMsg(new ChatMessage(ChatMessage.LOGOUT, ""));
                }
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

    private int isUserRegistered(String publicKey, ClientThread t) {
        for (UserKey anUserList : userList) {
            if (anUserList.getPublicKey().equals(publicKey)) {
                anUserList.setName(t.getUsername());
                return Integer.parseInt(anUserList.getPower());
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

    public void setPrintMsg(boolean printMsg) {
        this.printMsg = printMsg;
        for (ClientThread ct : clientList) {
            ct.setPrintMsg(printMsg);
        }
    }

    public void setAllowCanalChanging(boolean allowCanalChanging) {
        this.allowCanalChanging = allowCanalChanging;
        for (ClientThread ct : clientList) {
            ct.setAllowCanalChanging(allowCanalChanging);
        }
    }

    public UserKey getUser(String username, int index) {
        for (ClientThread ct : clientList) {
            if (ct.getUsername().equals(username) && ct.getCanal() == index) {
                return new UserKey(username, ct.getPublicKey(), String.valueOf(ct.getClientPower()));
            }
        }
        return new UserKey("0", "0", "0");
    }

    public void changeUserPower(UserKey user, int power, String text) {
        for (int i = 0; i < userList.size(); i++) {
            if (user.equals(userList.get(i)))
                userList.get(i).setPower(String.valueOf(power));
        }
        int index = 0;
        while (!canalList.get(index).getName().equals(text))
            index++;
        for (ClientThread ct : clientList) {
            if (ct.getUsername().equals(user.getName()) && ct.getCanal() == index) {
                ct.setClientPower(power);
            }
        }
    }

    public void kickUser(UserKey user, String text) {
        int index = 0;
        while (!canalList.get(index).getName().equals(text))
            index++;
        for (ClientThread ct : clientList) {
            if (ct.getUsername().equals(user.getName()) && ct.getCanal() == index) {
                ct.writeMsg(new ChatMessage(ChatMessage.KICK, "Zostales wyrzucony z serwera"));
            }
        }
    }
}
