package a47.server.service;

import a47.server.exception.ErrorMessage;
import a47.server.exception.InvalidUserOrPassException;
import a47.server.exception.UserAlreadyExistsException;
import a47.server.model.User;
import a47.server.model.request.Challenge;
import a47.server.model.response.ChallengeResponse;
import a47.server.security.PasswordHashing;
import a47.server.util.AuxMethods;
import a47.server.util.Constants;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@Service
public class AuthenticationService {
    private FileManagerService fileManagerService;

    private HashMap<Long, String> loggedInUsers;

    private HashMap<String, User> registeredUsers;

    private TreeMap<UUID, Challenge> usersChallengesRequest;

    public AuthenticationService(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
        this.loggedInUsers = new HashMap<>();
        this.registeredUsers = new HashMap<>();
        this.usersChallengesRequest = new TreeMap<>();
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

    public void logoutUser(long token){
        loggedInUsers.remove(token);
    }

    public void validateUser(long token){
        if(!validateToken(token))
            throw new InvalidUserOrPassException(ErrorMessage.CODE_SERVER_INV_USER, "Token invalid");
    }

    public boolean userExists(String username){
        return registeredUsers.containsKey(username);
    }

    public String getLoggedInUser(long token){
        return loggedInUsers.get(token);
    }

    private boolean validateToken(long token) {
        return loggedInUsers.containsKey(token);
    }

    private long generateToken(){ //TODO check this generation of token (dont like it)
        SecureRandom random = new SecureRandom();
        return random.nextLong();
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

    public Challenge createChallenge(User user)throws Exception{
        try {
            PublicKey userPubKey = AuxMethods.getPublicKeyFrom("server", user.getUsername());
            if(userPubKey != null){
                byte[] challenge = new byte[Constants.Challenge.SIZE];
                new SecureRandom().nextBytes(challenge);
                byte[] cipheredChallenge = AuxMethods.cipherWithKey(challenge, userPubKey);
                Challenge challengeObject = new Challenge(user.getUsername(), cipheredChallenge, new Date());
                Challenge challengeToSave = new Challenge(challengeObject.getUUID(), user.getUsername(), challenge, challengeObject.getGeneratedDate(), user);
                if(usersChallengesRequest.containsKey(challengeToSave.getUUID())) {
                    return null;
                }
                usersChallengesRequest.put(challengeToSave.getUUID(), challengeToSave);
                return challengeObject;
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void processChallenge(ChallengeResponse challengeResponse){
        Date actualDate = new Date();
        Challenge originalChallenge = usersChallengesRequest.getOrDefault(challengeResponse.getUUID(), null);
        if(originalChallenge != null){
            if((originalChallenge.getUUID().equals(challengeResponse.getUUID())) && (actualDate.getTime() < (originalChallenge.getGeneratedDate().getTime() +  Constants.Challenge.TIMEOUT)) && Arrays.equals(challengeResponse.getUnCipheredChallenge(), originalChallenge.getChallenge()))
                registerUser(originalChallenge.getUser());
        }
    }

}
