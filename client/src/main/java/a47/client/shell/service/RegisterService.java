package a47.client.shell.service;

import a47.client.Constants;
import a47.client.shell.model.Challenge;
import a47.client.shell.model.PublishPubKey;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class RegisterService {
    public void registerCA(String username, PublicKey publicKey) throws NoSuchAlgorithmException {
        RestTemplate restTemplate = new RestTemplate();
        Challenge challenge = restTemplate.postForObject(Constants.CA.PUBLISH_URL, new PublishPubKey(username,publicKey.getEncoded()), Challenge.class);
    }

    public void registerServer(String username, String password) {

    }
}
