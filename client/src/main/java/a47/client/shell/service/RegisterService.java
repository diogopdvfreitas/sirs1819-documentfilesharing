package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.Challenge;
import a47.client.shell.model.ChallengeResponse;
import a47.client.shell.model.PublishPubKey;
import a47.client.shell.model.RegisterUser;
import org.jboss.logging.Logger;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class RegisterService {
    private static Logger logger = Logger.getLogger(RegisterService.class);
    public boolean registerCA(String username, PublicKey publicKey) throws NoSuchAlgorithmException {
        RestTemplate restTemplate = new RestTemplate();
        Challenge challenge = restTemplate.postForObject(Constants.CA.PUBLISH_URL, new PublishPubKey(username, publicKey.getEncoded()), Challenge.class);
        byte[] unCipheredChallenge = AuxMethods.decipherWithPrivateKey(challenge.getChallenge(), ClientShell.keyManager.getPrivateKey());
        String result = restTemplate.postForObject(Constants.CA.PUBLISH_RESPONSE_URL, new ChallengeResponse(challenge.getUUID(), challenge.getUsername(), unCipheredChallenge), String.class);
        if(result.equals("ok"))
            return true;
        return false;
    }

    public boolean registerServer(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject(Constants.SERVER.REGISTER_SERVER_URL, new RegisterUser(username, password), String.class);
        if(result.equals("User registered with success"))
            return true;
        return false;
    }
}
