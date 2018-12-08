package a47.ca.controller;

import a47.ca.model.RequestPubKey;
import a47.ca.service.RequestService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
        byte[] pubkey = requestService.getPublicKey(requestPubKey).getEncoded();
        if(pubkey != null) {
            logger.info("PubKey from: " + requestPubKey.getUsernameToGetPubKey() + " sent to: " + requestPubKey.getUsername());
            return ResponseEntity.ok(pubkey);
        }else {
            logger.error("PublicKey: " + requestPubKey.getUsernameToGetPubKey() + " requested by: " + requestPubKey.getUsername() + " is not available." );
            return ResponseEntity.ok(null);
        }
    }

}
