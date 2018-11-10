package a47.ca.keyManager;

import a47.ca.model.Challenge;

import java.security.PublicKey;
import java.util.HashMap;

public class KeyManager {
    private static KeyManager keymanager = null;
    private HashMap<String, PublicKey> usersPublicKeys;

    private HashMap<String, Challenge> usersChallenges;

    //TODO CRIAR UMA THREAD PARA LIMPAR OS CHALLENGES EXPIRADOS
    public static synchronized KeyManager getInstance() {
        if(keymanager == null) {
            keymanager = new KeyManager();
        }
        return keymanager;
    }

    private KeyManager() {usersPublicKeys = new HashMap<>(); }

    public PublicKey getPublicKey(String username) {
        return usersPublicKeys.getOrDefault(username, null);
    }

    public boolean setPublicKey(String username, PublicKey publicKey) {
        if(!usersPublicKeys.containsKey(username)) {
            usersPublicKeys.put(username, publicKey);
            return true;
        } else {
            return false;
        }
    }

    public Challenge getChallenge(String username) {
        return usersChallenges.getOrDefault(username, null);
    }

    public boolean storeChallengeSent(String username, Challenge challenge) {
        if(!usersChallenges.containsKey(username)) {
            usersChallenges.put(username, challenge);
            return true;
        } else {
            return false;
        }
    }

}
