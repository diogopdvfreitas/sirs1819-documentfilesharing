package a47.client.shell.service;

import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.response.UserFileResponse;
import org.jboss.logging.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class ListFilesService {
    private static Logger logger = Logger.getLogger(ListFilesService.class);

    public List<UserFileResponse> ListFiles(long token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<UserFileResponse>> response = null;
        try {
            response = restTemplate.exchange(
                    Constants.FILE.LIST_FILE_SERVER_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<UserFileResponse>>(){});
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                ClientShell.setValidToken(false);
                return null;
            }
        }
        return response.getBody();
    }
}
