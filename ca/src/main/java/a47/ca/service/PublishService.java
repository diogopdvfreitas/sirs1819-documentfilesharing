package a47.ca.service;

import a47.ca.Constants;
import a47.ca.keyManager.AuxMethods;
import a47.ca.keyManager.KeyManager;
import a47.ca.model.Challenge;
import a47.ca.model.ChallengeResponse;
import a47.ca.model.PublishPubKey;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Service
public class PublishService {

    public Challenge createChallenge(PublishPubKey publishPubKey)throws Exception{
        // Issue a challengeResponsePublish to test if the client owns that public key
        byte[] challenge = new byte[Constants.Challenge.SIZE];
        new SecureRandom().nextBytes(challenge);
        byte[] cipheredChallenge = AuxMethods.cipherWithKey(challenge, publishPubKey.getPublicKey());
        Date actualDate = new Date();
        if(KeyManager.getInstance().storeChallengePublish(publishPubKey.getUsername(), new Challenge(publishPubKey.getUsername(), publishPubKey.getPublicKey(), challenge, actualDate)))
            return new Challenge(publishPubKey.getUsername(), publishPubKey.getPublicKey(), cipheredChallenge, actualDate);
        return null;
    }

    public boolean addPublicKey(ChallengeResponse challengeResponse) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Date actualDate = new Date();
        Challenge originalChallenge = KeyManager.getInstance().getChallengePublish(challengeResponse.getUsername());
        if(originalChallenge != null){
            if(actualDate.getTime() > (originalChallenge.getGeneratedDate().getTime() +  Constants.Challenge.TIMEOUT)){
                if(challengeResponse.getUnCipheredChallenge().equals(originalChallenge.getChallenge())){
                    if(KeyManager.getInstance().setPublicKey(originalChallenge.getUsername(), originalChallenge.getPublicKey()))
                        return true;
                }
            }
        }
        return false;
    }

}
