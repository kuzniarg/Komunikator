package Server.Model;

public class UserKey {

    private String name;
    private String publicKey;
    private String power;

    public UserKey(String publicKey, String power) {
        this.publicKey = publicKey;
        this.power = power;
        this.name = "brak";
    }

    public UserKey(String name, String publicKey, String power) {
        this.name = name;
        this.publicKey = publicKey;
        this.power = power;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof UserKey)) {
            return false;
        }
        UserKey that = (UserKey) other;
        return this.publicKey.equals(that.publicKey);
    }

    @Override
    public String toString() {
        return publicKey + ";" + power;
    }
}
