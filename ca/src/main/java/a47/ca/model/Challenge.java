package a47.ca.model;

import a47.ca.keyManager.AuxMethods;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

public class Challenge {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private byte[] publicKey;

    @NotNull
    @NotBlank
    private byte[] challenge;

    private String usernameToGetPubKey;
    private Date generatedDate;

    public Challenge(@NotNull @NotBlank String username, @NotNull @NotBlank PublicKey publicKey, @NotNull @NotBlank byte[] challenge, Date generatedDate) {
        this.username = username;
        this.publicKey = publicKey.getEncoded();
        this.challenge = challenge;
        this.generatedDate = generatedDate;
    }

    public Challenge(@NotNull @NotBlank String username, String usernameToGetPubKey, @NotNull @NotBlank byte[] challenge, Date generatedDate) {
        this.username = username;
        this.challenge = challenge;
        this.usernameToGetPubKey = usernameToGetPubKey;
        this.generatedDate = generatedDate;
    }

    public String getUsername() {
        return username;
    }

    public PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        return AuxMethods.decodePubKey(publicKey);
    }

    public byte[] getChallenge() {
        return challenge;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }

    public String getUsernameToGetPubKey() {
        return usernameToGetPubKey;
    }

}
