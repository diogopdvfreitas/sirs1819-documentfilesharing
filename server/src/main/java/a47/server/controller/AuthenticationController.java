package a47.server.controller;

import a47.server.model.RegisterUser;
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUser registerUser){
        logger.info("Registering user " + registerUser.getUsername());
        authenticationService.registerUser(registerUser);
        logger.info("Registration user " + registerUser.getUsername()+" done");
        return ResponseEntity.ok("User registered with success");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody RegisterUser registerUser){
        authenticationService.loginUser(registerUser);
        return ResponseEntity.ok("Logged In");
    }
}
