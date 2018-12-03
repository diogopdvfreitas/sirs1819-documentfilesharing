package a47.server.controller;

import a47.server.model.User;
import a47.server.service.AuthenticationService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
        logger.info("User: " + user.getUsername() + " registered with success");
        return ResponseEntity.ok(true);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody User user){
        return ResponseEntity.ok(authenticationService.loginUser(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("token") @NotNull @NotBlank long token){
        authenticationService.logoutUser(token);
        return ResponseEntity.ok("Logged Out");
    }
}
