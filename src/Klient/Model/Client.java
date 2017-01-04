package Klient.Model;

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

    public Client(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch (Exception ec) {
            display("Blad laczenia z serwerem: " + ec);
            return false;
        }

        display("Polaczono z " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Blad podczas tworzenia strumieni wejscia/wyjscia: " + eIO);
            return false;
        }

        new ListenFromServer(sInput).start();
        try {
            sOutput.writeObject(username);
        } catch (IOException eIO) {
            display("Blad podczas logowania: " + eIO);
            disconnect();
            return false;
        }
        return true;
    }

    private void display(String msg) {
        System.out.println(msg);
    }

    public void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Blad podczas wysylania wiadomosci: " + e);
        }
    }

    public void disconnect() {
        sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
        try {
            if (sInput != null) sInput.close();
            if (sOutput != null) sOutput.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
        }
    }
}

