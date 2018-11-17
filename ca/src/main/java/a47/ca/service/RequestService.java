package a47.ca.service;

import a47.ca.Constants;
import a47.ca.keyManager.AuxMethods;
import a47.ca.keyManager.KeyManager;
import a47.ca.model.Challenge;
import a47.ca.model.ChallengeResponse;
import a47.ca.model.RequestPubKey;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Date;

@Service
public class RequestService {

    public Challenge createChallenge(RequestPubKey requestPubKey)throws Exception{
        // Issue a challengeResponse to test if the client is non impersonating someone.
        PublicKey userPubKey = KeyManager.getInstance().getPublicKey(requestPubKey.getUsername());
        if(userPubKey != null){
            byte[] challenge = new byte[Constants.Challenge.SIZE];
            new SecureRandom().nextBytes(challenge);
            byte[] cipheredChallenge = AuxMethods.cipherWithKey(challenge, userPubKey);
            Challenge challengeObject = new Challenge(requestPubKey.getUsername(), requestPubKey.getUsernameToGetPubKey(), challenge, new Date());
            if(KeyManager.getInstance().storeChallengeRequest(challengeObject)){
                challengeObject.setChallenge(cipheredChallenge);
                return challengeObject;
            }
        }
        return null;
    }

    public PublicKey getPublicKey(ChallengeResponse challengeResponse){
        Date actualDate = new Date();
        Challenge originalChallenge = KeyManager.getInstance().getChallengeRequest(challengeResponse.getUsername());
        if(originalChallenge != null){
            if((originalChallenge.getUUID().equals(challengeResponse.getUUID())) && (actualDate.getTime() < (originalChallenge.getGeneratedDate().getTime() +  Constants.Challenge.TIMEOUT)) && challengeResponse.getUnCipheredChallenge().equals(originalChallenge.getChallenge()))
                    return KeyManager.getInstance().getPublicKey(originalChallenge.getUsernameToGetPubKey());
        }
        return null;
    }
}
