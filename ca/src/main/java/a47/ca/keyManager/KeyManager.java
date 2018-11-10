package a47.ca.keyManager;

import a47.ca.Constants;
import a47.ca.model.Challenge;

import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class KeyManager {
    private static KeyManager keymanager = null;
    private HashMap<String, PublicKey> usersPublicKeys;
    private HashMap<String, Challenge> usersChallengesPublish;
    private HashMap<String, Challenge> usersChallengesRequest;
    private Timer timer;

    public static synchronized KeyManager getInstance() {
        if(keymanager == null) {
            keymanager = new KeyManager();
        }
        return keymanager;
    }

    class deleteExpiredChallenges extends TimerTask {
        public void run() {
            usersChallengesPublish.forEach((user, challenge)-> {
                        if((new Date().getTime()) > (challenge.getGeneratedDate().getTime() +  Constants.Challenge.TIMEOUT))
                            usersChallengesPublish.remove(user);
                    }
            );
            usersChallengesRequest.forEach((user, challenge)-> {
                        if((new Date().getTime()) > (challenge.getGeneratedDate().getTime() +  Constants.Challenge.TIMEOUT))
                            usersChallengesRequest.remove(user);
                    }
            );
        }
    }

    private KeyManager() {
        usersPublicKeys = new HashMap<>();
        usersChallengesPublish = new HashMap<>();
        usersChallengesRequest = new HashMap<>();


        timer = new Timer();
        timer.schedule(new deleteExpiredChallenges(),
                0,        //initial delay
                3*1000);  //subsequent rate}
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

    public Challenge getChallengePublish(String username) {
        return usersChallengesPublish.getOrDefault(username, null);
    }

    public boolean storeChallengePublish(String username, Challenge challenge) {
        if(usersChallengesPublish.containsKey(username)) {
            return false;
        }
        usersChallengesPublish.put(username, challenge);
        return true;
    }

    public Challenge getChallengeRequest(String username) {
        return usersChallengesRequest.getOrDefault(username, null);
    }

    public boolean storeChallengeRequest(String username, Challenge challenge) {
        if(usersChallengesRequest.containsKey(username)) {
            return false;
        }
        usersChallengesRequest.put(username, challenge);
        return true;
    }

}
