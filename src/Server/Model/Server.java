package Server.Model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server implements Runnable {
    private int port;
    protected ArrayList<ClientThread> clientList;
    private SimpleDateFormat sdf;
    private boolean keepGoing;
    private int uniqueId;

    public Server(int port) {
        this.port = port;
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

                ClientThread t = new ClientThread(socket, ++uniqueId, clientList);
                clientList.add(t);
                t.start();
                Thread.sleep(10);
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
        } catch (InterruptedException e) {
            e.printStackTrace();
            display("Blad oczekiwania (sleep)");
        }
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
