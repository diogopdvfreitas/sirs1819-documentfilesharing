package a47.client.shell.service;

import a47.client.Constants;
import org.jboss.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class LogoutService {
    private static Logger logger = Logger.getLogger(LogoutService.class);

    public Boolean LogoutServer(long token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.postForObject(Constants.SERVER.LOGOUT_SERVER_URL, entity, Boolean.class);
    }
}
