package Server.Model;

import Klient.Model.ChatMessage;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
    private SimpleDateFormat sdf;
    private TreeItem<String> user;

    ClientThread(Socket socket, String clientID, ArrayList<ClientThread> clientList, TreeView<String> TreeServer, ArrayList<Canal> canalList) {
        this.clientID = clientID;
        this.socket = socket;
        this.canalList = canalList;
        this.TreeServer = TreeServer;
        this.clientList = clientList;
        this.clientPower = 0;
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput = new ObjectInputStream(socket.getInputStream());
            username = (String) sInput.readObject();
            sdf = new SimpleDateFormat("HH:mm:ss");
            display(username + " polaczyl sie z serwerem");
        } catch (IOException e) {
            display("Blad podczas tworzenia strumieni wejscia/wyjscia: " + e);
        } catch (ClassNotFoundException ignored) {
        }
        user = new TreeItem<>(username);
    }

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg + "\n";
        System.out.print(time);
    }

    public void run() {
        addClient(0);
        boolean keepGoing = true;
        while (keepGoing) {
            ChatMessage cm;
            try {
                cm = (ChatMessage) sInput.readObject();
            } catch (IOException e) {
                display(username + " blad podczas odczytu ze strumienia: " + e);
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
                    if (canalList.get(newCanal).getPower() <= clientPower)
                        changeCanal(newCanal);
                    else writeMsg(new ChatMessage(1, "Odmowa dostepu do kanalu " + canalList.get(newCanal).getName()));
                    break;
            }
        }
        remove();
        close();
    }

    private int findCanal(String message) {
        System.out.println(canalList);
        int index = 0;
        while (index < canalList.size() && !canalList.get(index).getName().equals(message))
            index++;
        System.out.println(canalList.get(index));
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

    private void close() {
        try {
            if (sOutput != null) sOutput.close();
            if (sInput != null) sInput.close();
            if (socket != null) socket.close();
        } catch (Exception ignored) {
        }
        display(username + " opuscil serwer");
    }

    private boolean writeMsg(ChatMessage msg) {
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

    public String getClientID() {
        return clientID;
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

    public void setCanal(int canal) {
        this.canal = canal;
    }

    public String getUsername() {
        return username;
    }
}
