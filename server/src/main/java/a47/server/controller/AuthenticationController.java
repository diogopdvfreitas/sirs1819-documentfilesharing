package a47.server.controller;

import a47.server.model.User;
import a47.server.service.AuthenticationService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    private static Logger logger = Logger.getLogger(AuthenticationController.class);
    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user){
        logger.info("Registering user " + user.getUsername());
        authenticationService.registerUser(user);
        logger.info("Registration user " + user.getUsername()+" done");
        return ResponseEntity.ok("User registered with success");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody User user){
        authenticationService.loginUser(user);
        return ResponseEntity.ok("Logged In");
    }
}
