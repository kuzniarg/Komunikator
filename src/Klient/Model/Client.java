package Klient.Model;

import javafx.scene.control.TreeView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;
    private String server, username;
    private int port;
    private TreeView<String> TreeClient;
    private ListenFromServer LFS;
    private RSA rsa;

    public Client(String server, int port, String username, TreeView<String> TreeClient, RSA rsa) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.TreeClient = TreeClient;
        this.rsa = rsa;
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch (Exception ec) {
            display("Blad laczenia z serwerem");
            return false;
        }

        display("Polaczono z " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Blad podczas tworzenia strumieni wejscia/wyjscia");
            return false;
        }

        try {
            sOutput.writeObject(username);
            sOutput.writeObject(rsa.getN().toString());
            sOutput.writeObject(rsa.getE().toString());
        } catch (IOException eIO) {
            display("Blad podczas logowania");
            disconnect();
            return false;
        }

        try {
            sOutput.writeObject(new ChatMessage(ChatMessage.AUTHORIZATION, "start"));
            ChatMessage cm = (ChatMessage) sInput.readObject();
            if (cm.getType() == ChatMessage.KICK) {
                display("Twoj klient zostal zbanowany na serwerze, z ktorym probujesz sie polaczyc");
                return false;
            }
            String answer = rsa.decrypt(cm.getMessage());
            sOutput.writeObject(new ChatMessage(ChatMessage.AUTHORIZATION, answer));
            cm = (ChatMessage) sInput.readObject();
            if (!cm.getMessage().equals("PASS")) {
                display("Blad autoryzacji");
                return false;
            } else {
                LFS = new ListenFromServer(sInput, TreeClient, this);
                LFS.start();
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        display("Blad autoryzacji");
        return false;
    }

    private void display(String msg) {
        System.out.println(msg);
    }

    public void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Blad podczas wysylania wiadomosci");
        }
    }

    public void disconnect() {
        if (sOutput != null)
            sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
        try {
            if (sInput != null) sInput.close();
            if (sOutput != null) sOutput.close();
            if (socket != null) socket.close();
        } catch (Exception ignored) {
        }
    }

    public void setTreeClient(TreeView<String> TreeClient) {
        this.TreeClient = TreeClient;
        LFS.setTreeClient(TreeClient);
    }
}

