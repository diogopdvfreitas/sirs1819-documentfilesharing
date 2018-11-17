package a47.ca.controller;

import a47.ca.model.Challenge;
import a47.ca.model.ChallengeResponse;
import a47.ca.model.RequestPubKey;
import a47.ca.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.PublicKey;

@RestController
public class RequestController {

    private RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/request")
    public ResponseEntity<?> request(@Valid @RequestBody RequestPubKey requestPubKey) throws Exception{
        Challenge challengeToSend = requestService.createChallenge(requestPubKey);
        if(challengeToSend != null) {
            return ResponseEntity.ok(challengeToSend);
        }else //deveria retornar que utilizador nao esta registado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/request/response")
    public ResponseEntity<?> challengeResponseRequest(@Valid @RequestBody ChallengeResponse challengeResponse) {
        PublicKey publicKeyToSend = requestService.getPublicKey(challengeResponse);
        if(publicKeyToSend != null) {
            return ResponseEntity.ok(publicKeyToSend);
        }else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
