package a47.ca.controller;

import a47.ca.model.PublishPubKey;
import a47.ca.service.PublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PublishController {

    private PublishService publishService;

    @Autowired
    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> registerUser(@Valid @RequestBody PublishPubKey publishPubKey){
        if(publishService.publishPubKey(publishPubKey))
            return ResponseEntity.ok("SIM");
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
