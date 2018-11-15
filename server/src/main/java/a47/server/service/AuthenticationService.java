package a47.server.service;

import a47.server.exception.ErrorMessage;
import a47.server.exception.UserAlreadyExistsException;
import a47.server.model.RegisterUser;
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

    public void registerUser(RegisterUser registerUser){//TODO hash password
        String users = FileUtil.readFile(usersFileName);
        HashMap<String, String> usersHash = getUsers(users);
        if(usersHash.containsKey(registerUser.getUsername())) //check if user already exists
            throw new UserAlreadyExistsException(ErrorMessage.CODE_SERVER_DUP_USER, "User already exists");
        FileUtil.writeToFile(usersFileName, registerUser.getUsername() + " " + registerUser.getPassword());
    }

    public boolean loginUser(RegisterUser registerUser){//TODO Compare with hash
        String users = FileUtil.readFile(usersFileName);
        HashMap<String, String> usersHash = getUsers(users);
        return usersHash.containsKey(registerUser.getUsername()) && usersHash.get(registerUser.getUsername()).equals(registerUser.getPassword());
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
