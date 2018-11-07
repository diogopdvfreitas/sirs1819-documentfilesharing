package a46.server.service;

import a46.server.model.RegisterUser;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    public boolean registerUser(RegisterUser registerUser){// dummy register
        return true;
    }
}
