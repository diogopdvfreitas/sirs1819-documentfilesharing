package a47.server.service;

import a47.server.exception.ErrorMessage;
import a47.server.exception.InvalidUserOrPassException;
import a47.server.exception.UserAlreadyExistsException;
import a47.server.model.User;
import a47.server.security.PasswordHashing;
import a47.server.util.FileUtil;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;

@Service
public class AuthenticationService {

    private final String usersFileName = "users.txt";

    private File usersFile = new File(usersFileName);

    @PostConstruct
    public void init() {
        try {
            usersFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerUser(User user){
        String users = FileUtil.readFile(usersFileName);
        HashMap<String, String> usersHash = getUsers(users);
        if(usersHash.containsKey(user.getUsername())) //check if user already exists
            throw new UserAlreadyExistsException(ErrorMessage.CODE_SERVER_DUP_USER, "User already exists");
        FileUtil.writeToFile(usersFileName, user.getUsername() + " " + PasswordHashing.createHashedPassword(user.getPassword()));
    }

    public void loginUser(User user){
        String users = FileUtil.readFile(usersFileName);
        HashMap<String, String> usersHash = getUsers(users);
        if(!usersHash.containsKey(user.getUsername()) || !PasswordHashing.validatePassword(user.getPassword(), usersHash.get(user.getUsername())))
            throw new InvalidUserOrPassException(ErrorMessage.CODE_SERVER_INV_USER, "Username or password invalid");
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
