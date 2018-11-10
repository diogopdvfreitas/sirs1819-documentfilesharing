package a47.ca.keyManager;

import a47.ca.model.Challenge;

import java.security.PublicKey;
import java.util.HashMap;

public class KeyManager {
    private static KeyManager keymanager = null;
    private HashMap<String, PublicKey> usersPublicKeys;
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
            return false;
        }
    }

}
