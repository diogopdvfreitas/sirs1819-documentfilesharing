package a47.ca.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Challenge {
    private UUID uuid;

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
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.publicKey = publicKey.getEncoded();
        this.challenge = challenge;
        this.generatedDate = generatedDate;
    }

    public Challenge(@NotNull @NotBlank String username, String usernameToGetPubKey, @NotNull @NotBlank byte[] challenge, Date generatedDate) {
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.challenge = challenge;
        this.usernameToGetPubKey = usernameToGetPubKey;
        this.generatedDate = generatedDate;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPublicKey() {
        return publicKey;
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

    public UUID getUUID() {
        return uuid;
    }

}
