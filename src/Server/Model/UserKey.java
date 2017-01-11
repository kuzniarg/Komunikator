package Server.Model;

public class UserKey {

    private String publicKey;
    private int power;

    public UserKey(String publicKey, int power) {
        this.publicKey = publicKey;
        this.power = power;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
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
