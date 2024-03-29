package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.Challenge;
import a47.client.shell.model.PublishPubKey;
import a47.client.shell.model.User;
import a47.client.shell.model.response.ChallengeResponse;
import org.jboss.logging.Logger;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;

public class RegisterService {
    private static Logger logger = Logger.getLogger(RegisterService.class);
    public Boolean registerCA(String username, PublicKey publicKey){
        RestTemplate restTemplate = new RestTemplate();
        Challenge challenge = restTemplate.postForObject(Constants.CA.PUBLISH_URL, new PublishPubKey(username, publicKey.getEncoded()), Challenge.class);
        byte[] unCipheredChallenge = AuxMethods.decipherWithPrivateKey(challenge.getChallenge(), ClientShell.keyManager.getPrivateKey());
        return restTemplate.postForObject(Constants.CA.PUBLISH_RESPONSE_URL, new ChallengeResponse(challenge.getUUID(), challenge.getUsername(), unCipheredChallenge), Boolean.class);
    }

    public Boolean registerServer(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        Challenge challenge = restTemplate.postForObject(Constants.SERVER.REGISTER_SERVER_URL, new User(username, password), Challenge.class);
        byte[] unCipheredChallenge = AuxMethods.decipherWithPrivateKey(challenge.getChallenge(), ClientShell.keyManager.getPrivateKey());
        return restTemplate.postForObject(Constants.SERVER.REGISTER_RESPONSE_SERVER_URL, new ChallengeResponse(challenge.getUUID(), challenge.getUsername(), unCipheredChallenge), Boolean.class);
    }
}
