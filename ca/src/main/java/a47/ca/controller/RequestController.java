package a47.ca.controller;

import a47.ca.model.Challenge;
import a47.ca.model.ChallengeResponse;
import a47.ca.model.RequestPubKey;
import a47.ca.service.RequestService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.PublicKey;

@RestController
public class RequestController {
    private static Logger logger = Logger.getLogger(PublishController.class);
    private RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> request(@Valid @RequestBody RequestPubKey requestPubKey) throws Exception{
        Challenge challengeToSend = requestService.createChallenge(requestPubKey);
        if(challengeToSend != null) {
            logger.info("Request Challenge sent to: " + challengeToSend.getUsername() + ". Requesting: " + challengeToSend.getUsernameToGetPubKey());
            return ResponseEntity.ok(challengeToSend);
        }else { //TODO: deveria retornar que utilizador nao esta registado
            logger.error("Generating Request Challenge");
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/request/response")
    public ResponseEntity<?> challengeResponseRequest(@Valid @RequestBody ChallengeResponse challengeResponse) {
        PublicKey publicKeyToSend = requestService.getPublicKey(challengeResponse);

        if(publicKeyToSend != null) {
            logger.info("PublicKey requested sent to: " + challengeResponse.getUsername());
            return ResponseEntity.ok(publicKeyToSend.getEncoded());
        }else{ //TODO:
            logger.error("Sending PublicKey to: " + challengeResponse.getUsername());
            return ResponseEntity.ok(false);
        }
    }
}
