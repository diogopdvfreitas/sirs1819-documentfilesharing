package a47.ca.service;

import a47.ca.keyManager.KeyManager;
import a47.ca.model.RequestPubKey;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

@Service
public class RequestService {
    public PublicKey getPublicKey(RequestPubKey requestPubKey){
        return KeyManager.getInstance().getPublicKey(requestPubKey.getUsernameToGetPubKey());
    }
}
