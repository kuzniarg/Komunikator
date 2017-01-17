package Klient.Model;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    public static final int CHANGE = 0, MESSAGE = 1, LOGOUT = 2, CANALS = 3, AUTHORIZATION = 4, KICK = 5;
    private static final long serialVersionUID = -826802655151735114L;
    private int type;
    private String message;

    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}

