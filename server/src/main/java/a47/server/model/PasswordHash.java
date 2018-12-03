package a47.server.model;

public class PasswordHash {
    private int iterations;
    private byte[] salt;
    private byte[] passwordHash;

    public PasswordHash(int iterations, byte[] salt, byte[] passwordHash) {
        this.iterations = iterations;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }

    public int getIterations() {
        return iterations;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }
}
