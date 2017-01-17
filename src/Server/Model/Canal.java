package Server.Model;

import java.util.ArrayList;

public class Canal {
    private String name;
    private int power;
    private ArrayList<User> users;

    public Canal(String name, int power) {
        this.name = name;
        this.power = power;
        this.users = new ArrayList<>();
    }

    public void addUser(String id, String name) {
        users.add(new User(id, name));
    }

    public void removeUser(String id, String name) {
        User tmp = new User(id, name);
        int i = 0;
        while (i < users.size() && !tmp.equals(users.get(i))) {
            i++;
        }
        if (!users.isEmpty() && i < users.size()) users.remove(i);
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder(name + "|" + power + "|");
        for (User user : users) tmp.append(user.getName()).append(";");
        return tmp.toString();
    }

    public String toStringWithoutUsers() {
        return name + ";" + power;
    }

    public boolean isUser(String id, String name) {
        User tmp = new User(id, name);
        int i = 0;
        while (i < users.size() && !tmp.equals(users.get(i))) {
            i++;
        }
        return !users.isEmpty() && i < users.size();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
