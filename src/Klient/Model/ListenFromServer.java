package Klient.Model;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

class ListenFromServer extends Thread {

    private ObjectInputStream sInput;
    private TreeView<String> TreeClient;
    private String lastCanals;
    private boolean isTreeReady = false;
    private Client client;

    ListenFromServer(ObjectInputStream sInput, TreeView<String> TreeClient, Client client) {
        this.sInput = sInput;
        this.TreeClient = TreeClient;
        this.client = client;
    }

    public void run() {
        while (true) {
            try {
                ChatMessage msg = (ChatMessage) sInput.readObject();
                switch (msg.getType()) {
                    case ChatMessage.MESSAGE:
                        System.out.println(msg.getMessage());
                        break;
                    case ChatMessage.CANALS:
                        refreshCanals(msg.getMessage());
                        break;
                    case ChatMessage.KICK:
                        System.out.println(msg.getMessage());
                        client.disconnect();
                        return;
                }
            } catch (IOException e) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                System.out.println(sdf.format(new Date()) + " Serwer zamknal polaczenie");
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }
    }

    private void refreshCanals(String message) {
        if (isTreeReady) {
            TreeItem<String> rootItem = new TreeItem<>("Serwer");
            rootItem.setExpanded(true);

            int end = message.indexOf(',');
            while (end > 0) {
                String line = message.substring(1, end);
                TreeItem<String> nodeItem;

                int separator1 = message.indexOf('|');
                String name = line.substring(0, separator1 - 1);
                nodeItem = new TreeItem<>(name);

                int separator2 = line.substring(separator1).indexOf('|');
                int power = Integer.parseInt(line.substring(separator1, separator1 + separator2));
                line = line.substring(separator1 + separator2 + 1);
                int idSign = line.indexOf(';');
                while (idSign > 0) {
                    nodeItem.getChildren().add(new TreeItem<>(line.substring(0, idSign)));
                    line = line.substring(idSign + 1);
                    idSign = line.indexOf(';');
                }

                nodeItem.setExpanded(true);
                rootItem.getChildren().add(nodeItem);

                message = message.substring(end + 1);
                end = message.indexOf(',');
                if (end < 0)
                    end = message.indexOf(']');
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    TreeClient.setRoot(rootItem);
                }
            });

        } else {
            lastCanals = message;
        }
    }

    void setTreeClient(TreeView<String> treeClient) {
        this.TreeClient = treeClient;
        isTreeReady = true;
        refreshCanals(lastCanals);
    }
}