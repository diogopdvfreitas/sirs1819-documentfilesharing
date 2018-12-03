package a47.client.shell.service;

import a47.client.Constants;
import a47.client.shell.model.User;
import org.jboss.logging.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class LoginService {
    private static Logger logger = Logger.getLogger(LoginService.class);
    private long token = -1;

    public boolean LoginServer(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            token = restTemplate.postForObject(Constants.SERVER.LOGIN_SERVER_URL, new User(username, password), long.class);
            if(token != -1){
                return true;
            }
            return false;
        }catch (HttpClientErrorException e){
            return false;
        }
    }

    public long getToken() {
        return token;
    }
}
