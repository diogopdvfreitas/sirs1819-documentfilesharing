package a47.ca.keyManager;

import a47.ca.model.Challenge;

import java.security.PublicKey;
import java.util.TreeMap;
import java.util.UUID;

public class KeyManager {
    private static KeyManager keymanager = null;
    private TreeMap<String, PublicKey> usersPublicKeys;
    private TreeMap<UUID, Challenge> usersChallengesPublish;
    private TreeMap<UUID, Challenge> usersChallengesRequest;

    public static synchronized KeyManager getInstance() {
        if(keymanager == null) {
            keymanager = new KeyManager();
        }
        return keymanager;
    }

    private KeyManager() {
        usersPublicKeys = new TreeMap<>();
        usersChallengesPublish = new TreeMap<>();
        usersChallengesRequest = new TreeMap<>();
    }

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

    public Challenge getChallengePublish(UUID uuid) {
        return usersChallengesPublish.getOrDefault(uuid, null);
    }

    public boolean putChallengePublish(Challenge challenge) {
        if(usersChallengesPublish.containsKey(challenge.getUUID())) {
            return false;
        }
        usersChallengesPublish.put(challenge.getUUID(), challenge);
        return true;
    }

    public Challenge getChallengeRequest(UUID uuid) {
        return usersChallengesRequest.getOrDefault(uuid, null);
    }

    public boolean storeChallengeRequest(Challenge challenge) {
        if(usersChallengesRequest.containsKey(challenge.getUUID())) {
            return false;
        }
        usersChallengesRequest.put(challenge.getUUID(), challenge);
        return true;
    }

}
