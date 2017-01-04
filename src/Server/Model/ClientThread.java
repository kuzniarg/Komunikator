package Server.Model;

import Klient.Model.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class ClientThread extends Thread {
    protected Socket socket;
    protected ObjectInputStream sInput;
    protected ObjectOutputStream sOutput;
    protected int id;
    protected ArrayList<ClientThread> clientList;
    private String username;
    private ChatMessage cm;
    private SimpleDateFormat sdf;

    public ClientThread(Socket socket, int id, ArrayList<ClientThread> clientList) {
        this.id = id;
        this.socket = socket;
        this.clientList = clientList;
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput = new ObjectInputStream(socket.getInputStream());
            username = (String) sInput.readObject();
            sdf = new SimpleDateFormat("HH:mm:ss");
            display(username + " polaczyl sie z serwerem");
        } catch (IOException e) {
            display("Blad podczas tworzenia strumieni wejscia/wyjscia: " + e);
            return;
        } catch (ClassNotFoundException e) {
        }
    }

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg + "\n";
        System.out.print(time);
    }

    public void run() {
        boolean keepGoing = true;
        while (keepGoing) {
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
                case ChatMessage.WHOISIN:
                    break;
            }
        }
        remove(id);
        close();
    }

    private void close() {
        try {
            if (sOutput != null) sOutput.close();
            if (sInput != null) sInput.close();
            if (socket != null) socket.close();
            display(username + " opuscil serwer");
        } catch (Exception ignored) {
        }
    }

    private boolean writeMsg(String msg) {
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
            ClientThread ct = clientList.get(i);
            if (!ct.writeMsg(message)) {
                clientList.remove(i);
                display("Nieaktywny klient " + ct.username + " usunity z listy.");
            }
        }
    }

    private synchronized void remove(int id) {
        for (int i = 0; i < clientList.size(); ++i) {
            ClientThread ct = clientList.get(i);
            if (ct.id == id) {
                clientList.remove(i);
                return;
            }
        }
    }
}
