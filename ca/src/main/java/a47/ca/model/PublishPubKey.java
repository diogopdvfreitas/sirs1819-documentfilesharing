package a47.ca.model;

import a47.ca.keyManager.AuxMethods;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class PublishPubKey {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private byte[] publicKey;

    public String getUsername() {
        return username;
    }

    public PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        return AuxMethods.decodePubKey(publicKey);
    }

}
