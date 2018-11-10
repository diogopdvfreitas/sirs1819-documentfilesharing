package a47.ca.controller;

import a47.ca.model.Challenge;
import a47.ca.model.ChallengeResponse;
import a47.ca.model.PublishPubKey;
import a47.ca.service.PublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
public class PublishController {

    private PublishService publishService;

    @Autowired
    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publish(@Valid @RequestBody PublishPubKey publishPubKey) throws Exception {
        Challenge challengeToSend = publishService.createChallenge(publishPubKey);
        if(challengeToSend != null) {
            return ResponseEntity.ok(challengeToSend);
        }else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/publish/response")
    public ResponseEntity<?> challengeResponsePublish(@Valid @RequestBody ChallengeResponse challengeResponse) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if(publishService.addPublicKey(challengeResponse)) {
            return ResponseEntity.ok("ok");
        }else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
