package Server.Model;

import Klient.Model.ChatMessage;
import Klient.Model.RSA;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

class ClientThread extends Thread {
    Socket socket;
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    private String clientID;
    private int clientPower;
    private int canal;
    private TreeView<String> TreeServer;
    private ArrayList<Canal> canalList;
    private ArrayList<ClientThread> clientList;
    private String username;
    private String publicKeyN;
    private String publicKeyE;
    private SimpleDateFormat sdf;
    private TreeItem<String> user;
    private boolean printMsg;
    private boolean allowCanalChanging;

    ClientThread(Socket socket, String clientID, ArrayList<ClientThread> clientList, TreeView<String> TreeServer,
                 ArrayList<Canal> canalList, boolean printMsg, boolean allowCanalChanging) {
        this.clientID = clientID;
        this.socket = socket;
        this.canalList = canalList;
        this.TreeServer = TreeServer;
        this.clientList = clientList;
        this.clientPower = 0;
        this.printMsg = printMsg;
        this.allowCanalChanging = allowCanalChanging;
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput = new ObjectInputStream(socket.getInputStream());
            username = (String) sInput.readObject();
            if (!username.equals("serwer")) {
                publicKeyN = (String) sInput.readObject();
                publicKeyE = (String) sInput.readObject();
                sdf = new SimpleDateFormat("HH:mm:ss");
                display(username + " laczy sie z serwerem");
            }
        } catch (IOException e) {
            display("Blad podczas tworzenia strumieni wejscia/wyjscia");
        } catch (ClassNotFoundException ignored) {
        }
        user = new TreeItem<>(username);
    }

    private void display(String msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (!username.equals("serwer")) {
                    String time = sdf.format(new Date()) + " " + msg + "\n";
                    System.out.print(time);
                }
            }
        });
    }

    public void run() {
        if (authorization()) {
            addClient(0);
            boolean keepGoing = true;
            while (keepGoing) {
                ChatMessage cm;
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException e) {
                    display(username + " blad podczas odczytu ze strumienia");
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }

                String message = cm.getMessage();
                switch (cm.getType()) {
                    case ChatMessage.MESSAGE:
                        broadcast(message);
                        break;
                    case ChatMessage.LOGOUT:
                        display(username + " wyslal zadanie przerwania polaczenia");
                        keepGoing = false;
                        break;
                    case ChatMessage.CHANGE:
                        int newCanal = findCanal(message);
                        if (newCanal == -1 || newCanal == canal)
                            break;
                        if (!allowCanalChanging && newCanal != 0) {
                            writeMsg(new ChatMessage(1, "Serwer zablokowal mozliwosc zmiany kanalu"));
                            break;
                        }
                        if (canalList.get(newCanal).getPower() <= clientPower)
                            changeCanal(newCanal);
                        else
                            writeMsg(new ChatMessage(1, "Odmowa dostepu do kanalu " + canalList.get(newCanal).getName()));
                        break;
                    case ChatMessage.KICK:
                        display(username + " zostal wyrzucony z serwera");
                        keepGoing = false;
                        break;
                }
            }
        }
        remove();
        close();
    }

    private boolean authorization() {
        try {
            ChatMessage cm = (ChatMessage) sInput.readObject();
            if (cm.getMessage().equals("serverRefresh")) {
                broadcastCanalsRefresh();
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        RSA rsa = new RSA(new BigInteger(publicKeyN), new BigInteger(publicKeyE));
        SecureRandom random = new SecureRandom();
        String code = new BigInteger(130, random).toString(32);
        String msg = rsa.encrypt(code);
        try {
            sOutput.writeObject(new ChatMessage(ChatMessage.AUTHORIZATION, msg));
            ChatMessage cm = (ChatMessage) sInput.readObject();
            if (cm.getMessage().equals(code)) {
                sOutput.writeObject(new ChatMessage(ChatMessage.AUTHORIZATION, "PASS"));
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int findCanal(String message) {
        int index = 0;
        while (index < canalList.size() && !canalList.get(index).getName().equals(message))
            index++;
        if (index == canalList.size())
            return -1;
        return index;
    }

    private synchronized void addClient(int newCanal) {
        this.canal = newCanal;
        canalList.get(newCanal).addUser(clientID, username);
        TreeServer.getRoot().getChildren().get(newCanal).getChildren().add(user);
        broadcast("dolaczyl do kanalu " + canalList.get(canal).getName());
        broadcastCanals();
    }

    private synchronized void removeClient() {
        TreeServer.getRoot().getChildren().get(canal).getChildren().remove(user);
        canalList.get(canal).removeUser(clientID, username);
        if (!this.username.equals("serwer"))
            broadcast("opuscil kanal " + canalList.get(canal).getName());
        this.canal = -1;
        broadcastCanals();
    }

    private synchronized void changeCanal(int newCanal) {
        removeClient();
        addClient(newCanal);
    }

    private synchronized void broadcastCanals() {
        String canalCode = canalList.toString();
        for (int i = clientList.size(); --i >= 0; ) {
            ClientThread ct = clientList.get(i);
            if (!ct.writeMsg(new ChatMessage(3, canalCode))) {
                clientList.remove(i);
                display("Nieaktywny klient " + ct.username + " usunity z listy.");
            }
        }
    }

    private synchronized void broadcastCanalsRefresh() {
        for (int index = 0; index < canalList.size(); index++) {
            ArrayList<User> list = canalList.get(index).getUsers();
            for (int index2 = 0; index2 < list.size(); index2++) {
                int index3 = 0;
                while (!clientList.get(index3).getUsername().equals(canalList.get(index).getUsers().get(index2).getName())
                        || !clientList.get(index3).getClientID().equals(canalList.get(index).getUsers().get(index2).getID()))
                    index3++;
                clientList.get(index3).setCanal(index);
            }
        }
        String canalCode = canalList.toString();
        for (int i = clientList.size() - 1; --i >= 0; ) {
            ClientThread ct = clientList.get(i);
            if (!ct.writeMsg(new ChatMessage(ChatMessage.CANALS, canalCode))) {
                clientList.remove(i);
                display("Nieaktywny klient " + ct.username + " usunity z listy.");
            }
        }
    }

    private void close() {
        try {
            if (sOutput != null) sOutput.close();
            if (sInput != null) sInput.close();
            if (socket != null) socket.close();
        } catch (Exception ignored) {
        }
        display(username + " opuscil serwer");
    }

    boolean writeMsg(ChatMessage msg) {
        if (!socket.isConnected()) {
            close();
            return false;
        }
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Blad podczas wysylania wiadomosci do " + username + ": " + e);
        }
        return true;
    }

    private synchronized void broadcast(String msg) {
        String time = sdf.format(new Date());
        String message = time + " " + username + ": " + msg;
        if (printMsg)
            System.out.println(message);

        for (int i = clientList.size(); --i >= 0; ) {
            if (clientList.get(i).getCanal() == canal) {
                ClientThread ct = clientList.get(i);
                if (!ct.writeMsg(new ChatMessage(1, message))) {
                    clientList.remove(i);
                    display("Nieaktywny klient " + ct.username + " usunity z listy.");
                }
            }
        }
    }

    private synchronized void remove() {
        for (int i = 0; i < clientList.size(); ++i) {
            ClientThread ct = clientList.get(i);
            if (Objects.equals(ct.clientID, clientID)) {
                clientList.remove(i);
                break;
            }
        }
        removeClient();
    }

    public String getPublicKey() {
        return publicKeyN;
    }

    public int getClientPower() {
        return clientPower;
    }

    public void setClientPower(int clientPower) {
        this.clientPower = clientPower;
    }

    public int getCanal() {
        return canal;
    }

    public void setPrintMsg(boolean printMsg) {
        this.printMsg = printMsg;
    }

    public void setAllowCanalChanging(boolean allowCanalChanging) {
        this.allowCanalChanging = allowCanalChanging;
    }

    public String getClientID() {
        return clientID;
    }

    public String getUsername() {
        return username;
    }

    public void setCanal(int canal) {
        this.canal = canal;
    }

    public void banned() {
        try {
            ChatMessage cm = (ChatMessage) sInput.readObject();
            sOutput.writeObject(new ChatMessage(ChatMessage.KICK, ""));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        display(username + " jest zbanowany - odrzucono probe polaczenia");
    }
}
