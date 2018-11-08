package a47.ca.keyManager;

import java.util.HashMap;

public class KeyManager {
    private static KeyManager keymanager = null;
    //private HashMap<String, PublicKey> usersPublicKeys;
    private HashMap<String, String> usersPublicKeys;

    public static synchronized KeyManager getInstance() {
        if(keymanager == null) {
            keymanager = new KeyManager();
        }
        return keymanager;
    }

    private KeyManager() {
        usersPublicKeys = new HashMap<>();
    }

    //public PublicKey getPublicKey(String username) {
    public String getPublicKey(String username) {
        return usersPublicKeys.getOrDefault(username, null);
    }

    //public boolean setPublicKey(String username, PublicKey publicKey) {
    public boolean setPublicKey(String username, String publicKey) {
        if( ! usersPublicKeys.containsKey(username)) {
            usersPublicKeys.put(username, publicKey);
            return true;
        } else return false;
    }

}
