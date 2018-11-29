package a47.ca.service;

import a47.ca.Constants;
import a47.ca.keyManager.AuxMethods;
import a47.ca.keyManager.KeyManager;
import a47.ca.model.Challenge;
import a47.ca.model.ChallengeResponse;
import a47.ca.model.PublishPubKey;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;

@Service
public class PublishService {

    public Challenge createChallenge(PublishPubKey publishPubKey)throws Exception{
        // Issue a challengeResponsePublish to test if the client owns that public key
        byte[] challenge = new byte[Constants.Challenge.SIZE];
        new SecureRandom().nextBytes(challenge);
        PublicKey publicKey = AuxMethods.decodePubKey(publishPubKey.getPublicKey());
        byte[] cipheredChallenge = AuxMethods.cipherWithKey(challenge, publicKey);
        Challenge challengeObject = new Challenge(publishPubKey.getUsername(), publishPubKey.getPublicKey(), cipheredChallenge, new Date());
        Challenge challengeToSave = new Challenge(challengeObject.getUUID(), publishPubKey.getUsername(), publishPubKey.getPublicKey(), challenge, challengeObject.getGeneratedDate());
        if(KeyManager.getInstance().putChallengePublish(challengeToSave)) {
            return challengeObject;
        }
        return null;
    }

    public boolean addPublicKey(ChallengeResponse challengeResponse) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Date actualDate = new Date();
        Challenge originalChallenge = KeyManager.getInstance().getChallengePublish(challengeResponse.getUUID());
        if(originalChallenge != null){
            if(originalChallenge.getUUID().equals(challengeResponse.getUUID()) && (actualDate.getTime() < (originalChallenge.getGeneratedDate().getTime() +  Constants.Challenge.TIMEOUT)) && Arrays.equals(challengeResponse.getUnCipheredChallenge(), originalChallenge.getChallenge()))
                    if(KeyManager.getInstance().setPublicKey(originalChallenge.getUsername(), AuxMethods.decodePubKey(originalChallenge.getPublicKey())))
                        return true;
        }

        return false;
    }

}
