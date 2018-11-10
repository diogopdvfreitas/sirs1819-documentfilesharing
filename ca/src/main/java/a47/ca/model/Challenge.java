package a47.ca.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.PublicKey;
import java.util.Date;

public class Challenge {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private PublicKey publicKey;

    @NotNull
    @NotBlank
    private byte[] challenge;


    private Date generatedDate;

    public Challenge(@NotNull @NotBlank String username, @NotNull @NotBlank PublicKey publicKey, @NotNull @NotBlank byte[] challenge, Date generatedDate) {
        this.username = username;
        this.publicKey = publicKey;
        this.challenge = challenge;
        this.generatedDate = generatedDate;
    }

    public String getUsername() {
        return username;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] getChallenge() {
        return challenge;
    }

    public Date getGeneratedDate() {
        return generatedDate;
    }


}
