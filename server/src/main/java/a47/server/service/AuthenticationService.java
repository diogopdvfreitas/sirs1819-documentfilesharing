package a47.server.service;

import a47.server.exception.ErrorMessage;
import a47.server.exception.InvalidUserOrPassException;
import a47.server.exception.UserAlreadyExistsException;
import a47.server.model.User;
import a47.server.security.PasswordHashing;
import org.springframework.stereotype.Service;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

@Service
public class AuthenticationService {
    private FileManagerService fileManagerService;

    private HashMap<Long, String> loggedInUsers;

    private HashMap<String, User> registeredUsers;

    public AuthenticationService(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
        this.loggedInUsers = new HashMap<>();
        this.registeredUsers = new HashMap<>();
    }

    public void registerUser(User newUser){
        if(registeredUsers.containsKey(newUser.getUsername())) //check if user already exists
            throw new UserAlreadyExistsException(ErrorMessage.CODE_SERVER_DUP_USER, "User already exists");
        registeredUsers.put(newUser.getUsername(), new User(newUser.getUsername(), PasswordHashing.createHashedPassword(newUser.getPassword())));
        fileManagerService.addUser(newUser.getUsername());
    }

    public long loginUser(User user){
        if(!registeredUsers.containsKey(user.getUsername()) || !PasswordHashing.validatePassword(user.getPassword(), registeredUsers.get(user.getUsername()).getPasswordHash()))
            throw new InvalidUserOrPassException(ErrorMessage.CODE_SERVER_INV_USER, "Username or password invalid");
        long token = generateToken();
        loggedInUsers.put(token, user.getUsername());
        return token;
    }

    public void validateUser(long token){
        if(!validateToken(token))
            throw new InvalidUserOrPassException(ErrorMessage.CODE_SERVER_INV_USER, "Token invalid");
    }

    public String getLoggedInUser(long token){
        return loggedInUsers.get(token);
    }

    private boolean validateToken(long token) {
        return loggedInUsers.containsKey(token);
    }

    private long generateToken(){ //TODO check this generation of token (dont like it)
        return (new BigInteger(64, new Random())).longValue();
    }

    private HashMap<String,String> getUsers(String users){
        HashMap<String,String> hashMap = new HashMap<>();
        String[] split = users.split("\n");
        for (String line: split) {
            String pair[] = line.split(" ");
            if(pair.length == 1)
                break;
            hashMap.put(pair[0], pair[1]);
        }
        return hashMap;
    }
}
