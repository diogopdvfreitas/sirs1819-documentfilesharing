package a47.client;

import org.jboss.logging.Logger;

import java.security.*;

public class KeyManager {
    private static KeyManager manager;
    private static Logger logger = Logger.getLogger(KeyManager.class);

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private byte[] password;

    public KeyManager() {
        generatekeys();
    }

    private void generatekeys(){
        ////// REMOVER //////////////////////////////
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
            kpg.initialize(2048);
        } catch (
                NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeyPair kp = kpg.generateKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
        ///////////////////////////////////////////////////
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

}
