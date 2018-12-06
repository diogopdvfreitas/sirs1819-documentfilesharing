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
import java.util.Arrays;
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
            Challenge challengeObject = new Challenge(requestPubKey.getUsername(), requestPubKey.getUsernameToGetPubKey(), cipheredChallenge, new Date());
            Challenge challengeToSave = new Challenge(challengeObject.getUUID(), requestPubKey.getUsername(), requestPubKey.getUsernameToGetPubKey(), challenge, challengeObject.getGeneratedDate());
            if(KeyManager.getInstance().storeChallengeRequest(challengeToSave)){
                return challengeObject;
            }
        }
        return null;
    }

    public PublicKey getPublicKey(ChallengeResponse challengeResponse){
        Date actualDate = new Date();
        System.out.println(1);
        Challenge originalChallenge = KeyManager.getInstance().getChallengeRequest(challengeResponse.getUUID());
        System.out.println(2);
        if(originalChallenge != null){
            System.out.println(3);
            if((originalChallenge.getUUID().equals(challengeResponse.getUUID()))) System.out.println(4);
            if((actualDate.getTime() < (originalChallenge.getGeneratedDate().getTime() +  Constants.Challenge.TIMEOUT))) System.out.println(5);
            if(Arrays.equals(challengeResponse.getUnCipheredChallenge(), originalChallenge.getChallenge())) System.out.println(6);
            if((originalChallenge.getUUID().equals(challengeResponse.getUUID())) && (actualDate.getTime() < (originalChallenge.getGeneratedDate().getTime() +  Constants.Challenge.TIMEOUT)) && Arrays.equals(challengeResponse.getUnCipheredChallenge(), originalChallenge.getChallenge()))
                    return KeyManager.getInstance().getPublicKey(originalChallenge.getUsernameToGetPubKey());
        }
        return null;
    }
}
