package a47.client;

import org.jboss.logging.Logger;

import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyManager {
    private static KeyManager manager;
    private static Logger logger = Logger.getLogger(KeyManager.class);

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private byte[] password;
    private long sessionID;

    public KeyManager() { }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }
}
