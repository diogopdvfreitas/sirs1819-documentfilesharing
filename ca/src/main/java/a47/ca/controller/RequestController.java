package a47.ca.controller;

import a47.ca.exception.ErrorMessage;
import a47.ca.exception.PublicKeyNotFoundException;
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
    public ResponseEntity<?> request(@Valid @RequestBody RequestPubKey requestPubKey) {
        PublicKey publicKey = requestService.getPublicKey(requestPubKey);
        if(publicKey == null){
            logger.error("PublicKey: " + requestPubKey.getUsernameToGetPubKey() + " is not available." );
            throw new PublicKeyNotFoundException(ErrorMessage.CODE_SERVER_GENERAL, "Failed getting public key");
        }
        byte[] pubkey = publicKey.getEncoded();
        logger.info("PubKey from: " + requestPubKey.getUsernameToGetPubKey() + " sent");
        return ResponseEntity.ok(pubkey);
    }

}
