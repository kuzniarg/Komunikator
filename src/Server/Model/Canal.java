package Server.Model;

import java.util.ArrayList;

public class Canal {
    private String name;
    private int power;
    private ArrayList<User> users;

    Canal(String name, int power) {
        this.name = name;
        this.power = power;
        this.users = new ArrayList<>();
    }

    void addUser(String id, String name) {
        users.add(new User(id, name));
    }

    void removeUser(String id, String name) {
        User tmp = new User(id, name);
        int i = 0;
        while (!tmp.equals(users.get(i)) && i < users.size()) {
            i++;
        }
        users.remove(i);
    }

    String getName() {
        return name;
    }

    int getPower() {
        return power;
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder(name + "|" + power + "|");
        for (User user : users) tmp.append(user.getName()).append(";");
        return tmp.toString();
    }
}
