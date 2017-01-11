package Klient.Model;

import javafx.beans.property.SimpleStringProperty;

public class ServerElement {

    private final SimpleStringProperty name;
    private final SimpleStringProperty IP;
    private final SimpleStringProperty port;

    public ServerElement(String name, String IP, String port) {
        this.name = new SimpleStringProperty(name);
        this.IP = new SimpleStringProperty(IP);
        this.port = new SimpleStringProperty(port);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String fName) {
        name.set(fName);
    }

    public String getIP() {
        return IP.get();
    }

    public void setIP(String fName) {
        IP.set(fName);
    }

    public String getPort() {
        return port.get();
    }

    public void setPort(String fName) {
        port.set(fName);
    }

    @Override
    public String toString() {
        return name.getValue() + ";" + IP.getValue() + ";" + port.getValue();
    }
}