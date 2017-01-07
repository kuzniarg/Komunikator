package Server.Model;

public class User {

    private String ID;
    private String name;

    User(String ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        User that = (User) other;
        return this.ID.equals(that.ID);
    }
}
