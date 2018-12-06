package a47.ca.controller;

import a47.ca.model.Challenge;
import a47.ca.model.ChallengeResponse;
import a47.ca.model.PublishPubKey;
import a47.ca.service.PublishService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
public class PublishController {
    private static Logger logger = Logger.getLogger(PublishController.class);
    private PublishService publishService;

    @Autowired
    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publish(@Valid @RequestBody PublishPubKey publishPubKey) throws Exception {
        Challenge challengeToSend = publishService.createChallenge(publishPubKey);
        if(challengeToSend != null) {
            logger.info("Publish Challenge sent to: " + challengeToSend.getUsername());
            return ResponseEntity.ok(challengeToSend);
        }else{ //TODO:
            logger.error("Generating Publish Challenge");
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/publish/response")
    public ResponseEntity<?> challengeResponsePublish(@Valid @RequestBody ChallengeResponse challengeResponse) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if(publishService.addPublicKey(challengeResponse)) {
            logger.info("Publish PublicKey completed: " + challengeResponse.getUsername());
            return ResponseEntity.ok(true);
        }else{ //TODO:
            logger.error("Publish PublicKey: " + challengeResponse.getUsername());
            return ResponseEntity.ok(false);
        }
    }
}
