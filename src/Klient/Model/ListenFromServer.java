package Klient.Model;

import java.io.IOException;
import java.io.ObjectInputStream;

class ListenFromServer extends Thread {

    private ObjectInputStream sInput;

    public ListenFromServer(ObjectInputStream sInput) {
        this.sInput = sInput;
    }

    public void run() {
        while (true) {
            try {
                String msg = (String) sInput.readObject();
                System.out.println(msg);
            } catch (IOException e) {
                System.out.println("Serwer zamknal polaczenie: " + e);
                break;
            } catch (ClassNotFoundException e2) {
            }
        }
    }
}