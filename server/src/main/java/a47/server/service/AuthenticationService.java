package a47.server.service;

import a47.server.model.RegisterUser;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;

@Service
public class AuthenticationService {

    private File usersFile = new File("users.txt");

    @PostConstruct
    public void init() {
        try {
            usersFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(RegisterUser registerUser){//TODO hash password and add custom exceptions
        String users = readFile("users.txt");
        HashMap<String, String> usersHash = getUsers(users);
        if(usersHash.containsKey(registerUser.getUsername()))
            return false;
        writeToFile("users.txt", registerUser.getUsername() + " " + registerUser.getPassword());
        return true;
    }

    public boolean loginUser(RegisterUser registerUser){//TODO Compare with hash
        String users = readFile("users.txt");
        HashMap<String, String> usersHash = getUsers(users);
        return usersHash.containsKey(registerUser.getUsername()) && usersHash.get(registerUser.getUsername()).equals(registerUser.getPassword());
    }

    //TODO move bellow methods to utils package
    private void writeToFile(String fileName, String text){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true));
            bufferedWriter.write(text);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
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
