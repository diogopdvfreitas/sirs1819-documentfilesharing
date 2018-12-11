package a47.server.controller;

import a47.server.model.replication.ServerSnapshot;
import a47.server.service.ReplicationService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("repli")
public class ReplicationController {
    private static Logger logger = Logger.getLogger(ReplicationController.class);

    private ReplicationService replicationService;

    @Autowired
    public ReplicationController(ReplicationService replicationService) {
        this.replicationService = replicationService;
    }

    @PostMapping("/registerReplica")
    public ResponseEntity<?> registerReplica(@Valid @RequestBody String url){
        logger.info("Registering server with URL: " + url);
        replicationService.registerReplica(url);
        logger.info("Server registered with success. URL: " + url);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/replicatePrimary")
    public ResponseEntity<?> replicatePrimary(@Valid @RequestBody ServerSnapshot snapshot){
        logger.info("Primary Server Snapshot received");
        replicationService.replicatePrimary(snapshot);
        logger.info("Snapshot applied, Primary Server Replicated with success");
        return ResponseEntity.ok(true);
    }

    @GetMapping("/turnPrimary")
    public ResponseEntity<?> turnPrimary(){
        logger.info("Primary server shutting down");
        logger.info("I'll now become the Primary Server!");
        replicationService.turnPrimary();
        return ResponseEntity.ok(true);
    }

    @PostMapping("/changePrimary")
    public ResponseEntity<?> changePrimary(@Valid @RequestBody String url){
        logger.info("Primary server switched to: " + url);
        replicationService.changePrimary(url);
        return ResponseEntity.ok(true);
    }
}
