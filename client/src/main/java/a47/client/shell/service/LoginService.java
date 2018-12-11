package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.Challenge;
import a47.client.shell.model.User;
import a47.client.shell.model.response.ChallengeResponse;
import org.jboss.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class LoginService {
    private static Logger logger = Logger.getLogger(LoginService.class);
    private long token = -1;

    public Boolean LoginServer(String username, String password) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Challenge challenge = restTemplate.postForObject(Constants.SERVER.LOGIN_SERVER_URL, new User(username, password), Challenge.class);
            byte[] unCipheredChallenge = AuxMethods.decipherWithPrivateKey(challenge.getChallenge(), ClientShell.keyManager.getPrivateKey());
            token = restTemplate.postForObject(Constants.SERVER.LOGIN_RESPONSE_SERVER_URL, new ChallengeResponse(challenge.getUUID(), challenge.getUsername(), unCipheredChallenge), long.class);
            if(token != -1){
                return true;
            }
            return false;
        }catch (HttpClientErrorException e){
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                ClientShell.setValidToken(false);
                return null;
            }
            return null;
        }
    }

    public long getToken() {
        return token;
    }
}
