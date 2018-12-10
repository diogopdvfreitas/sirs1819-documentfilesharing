package a47.server.service;

import a47.server.exception.BlockedUserException;
import a47.server.exception.ErrorMessage;
import a47.server.exception.InvalidUserOrPassException;
import a47.server.exception.UserAlreadyExistsException;
import a47.server.model.Session;
import a47.server.model.User;
import a47.server.model.request.Challenge;
import a47.server.model.response.ChallengeResponse;
import a47.server.security.PasswordHashing;
import a47.server.util.AuxMethods;
import a47.server.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.*;

@Service
public class AuthenticationService {
    @Value("${server.token.expiration}")
    private int expirationTime;

    private FileManagerService fileManagerService;

    private HashMap<Long, Session> loggedInUsers;

    private HashMap<String, User> registeredUsers;

    private TreeMap<UUID, Challenge> usersChallengesRequest;

    public AuthenticationService(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
        this.loggedInUsers = new HashMap<>();
        this.registeredUsers = new HashMap<>();
        this.usersChallengesRequest = new TreeMap<>();
    }

    private void registerUser(User newUser){
        if(registeredUsers.containsKey(newUser.getUsername())) //check if user already exists
            throw new UserAlreadyExistsException(ErrorMessage.CODE_SERVER_DUP_USER, "User already exists");
        registeredUsers.put(newUser.getUsername(), new User(newUser.getUsername(), PasswordHashing.createHashedPassword(newUser.getPassword())));
        fileManagerService.addUser(newUser.getUsername());
    }

    private long loginUser(User user){
        if(!registeredUsers.containsKey(user.getUsername()))
            throw new InvalidUserOrPassException(ErrorMessage.CODE_SERVER_INV_USER, "Username or password invalid");
        if(!PasswordHashing.validatePassword(user.getPassword(), registeredUsers.get(user.getUsername()).getPasswordHash())){
            registeredUsers.get(user.getUsername()).incLoginTries();
            if(registeredUsers.get(user.getUsername()).getLoginTries() > 3)
                throw new BlockedUserException(ErrorMessage.CODE_SERVER_USER_BLOCKED, "User blocked for excess of login tries");
            throw new InvalidUserOrPassException(ErrorMessage.CODE_SERVER_INV_USER, "Username or password invalid");
        }
        if(registeredUsers.get(user.getUsername()).getLoginTries() > 3)
            throw new BlockedUserException(ErrorMessage.CODE_SERVER_USER_BLOCKED, "User blocked for excess of login tries");
        long token = generateToken();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, expirationTime);
        Session session = new Session(token, user.getUsername(), cal.getTime());
        loggedInUsers.put(token, session);
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
        return loggedInUsers.get(token).getUsername();
    }

    private boolean validateToken(long token) {
        if(!loggedInUsers.containsKey(token))
            return false;
        if(loggedInUsers.get(token).getExpirationDate().after(new Date()))
            return true;
        else
            loggedInUsers.remove(token);
        return false;
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

    public Challenge createChallenge(User user){

        PublicKey userPubKey = AuxMethods.getPublicKeyFrom("server", user.getUsername());
        if(userPubKey != null){
            byte[] challenge = new byte[Constants.Challenge.SIZE];
            new SecureRandom().nextBytes(challenge);
            byte[] cipheredChallenge = new byte[0];
            try {
                cipheredChallenge = AuxMethods.cipherWithKey(challenge, userPubKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Challenge challengeObject = new Challenge(user.getUsername(), cipheredChallenge, new Date());
            Challenge challengeToSave = new Challenge(challengeObject.getUUID(), user.getUsername(), challenge, challengeObject.getGeneratedDate(), user);
            if(usersChallengesRequest.containsKey(challengeToSave.getUUID())) {
                return null;
            }
            usersChallengesRequest.put(challengeToSave.getUUID(), challengeToSave);
            return challengeObject;
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

    public long processChallengeLogin(ChallengeResponse challengeResponse){
        Date actualDate = new Date();
        Challenge originalChallenge = usersChallengesRequest.getOrDefault(challengeResponse.getUUID(), null);
        if(originalChallenge != null){
            if((originalChallenge.getUUID().equals(challengeResponse.getUUID())) && (actualDate.getTime() < (originalChallenge.getGeneratedDate().getTime() +  Constants.Challenge.TIMEOUT)) && Arrays.equals(challengeResponse.getUnCipheredChallenge(), originalChallenge.getChallenge()))
                return loginUser(originalChallenge.getUser());
        }
        return -1;
    }
}
