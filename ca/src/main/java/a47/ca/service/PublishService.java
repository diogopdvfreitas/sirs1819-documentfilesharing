package a47.ca.service;

import a47.ca.keyManager.KeyManager;
import a47.ca.model.PublishPubKey;
import org.springframework.stereotype.Service;

@Service
public class PublishService {
    public boolean publishPubKey(PublishPubKey publishPubKey){// dummy register
        //TODO VER ERROS
        return KeyManager.getInstance().setPublicKey(publishPubKey.getUsername(),publishPubKey.getPublicKey());
    }
}
