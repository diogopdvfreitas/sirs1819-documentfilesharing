package a47.server.service;

import a47.server.Constants;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RegisterService {
    private static Logger logger = Logger.getLogger(RegisterService.class);

    public Boolean registerPrimary(String port){
        String url = Constants.SEC_SERVER_BASE_URL + port;
        logger.info("Registering Replica with URL " + url + " on Primary Server");
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(Constants.SERVER.REGISTER_SERVER_URL, url, Boolean.class);
    }
}
