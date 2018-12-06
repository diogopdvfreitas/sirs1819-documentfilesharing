package a47.ca.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Challenge {
    private UUID uuid;

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotEmpty
    private byte[] publicKey;

    @NotNull
    @NotEmpty
    private byte[] challenge;

    private String usernameToGetPubKey;
    private Date generatedDate;

    public Challenge() {
    }

    public Challenge(UUID uuid, @NotNull @NotBlank String username, @NotNull @NotEmpty byte[] publicKey, @NotNull @NotEmpty byte[] challenge, Date generatedDate) {
        this.uuid = uuid;
        this.username = username;
        this.publicKey = publicKey;
        this.challenge = challenge;
        this.generatedDate = generatedDate;
    }

    public Challenge(@NotNull @NotBlank String username, @NotNull @NotEmpty byte[] publicKey, @NotNull @NotEmpty byte[] challenge, Date generatedDate) {
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.publicKey = publicKey;
        this.challenge = challenge;
        this.generatedDate = generatedDate;
    }

    public Challenge(@NotNull @NotBlank String username, String usernameToGetPubKey, @NotNull @NotEmpty byte[] challenge, Date generatedDate) {
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.challenge = challenge;
        this.usernameToGetPubKey = usernameToGetPubKey;
        this.generatedDate = generatedDate;
    }

    public Challenge(UUID uuid, @NotNull @NotBlank String username, String usernameToGetPubKey, @NotNull @NotEmpty byte[] challenge, Date generatedDate) {
        this.uuid = uuid;
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


    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public void setChallenge(byte[] challenge) {
        this.challenge = challenge;
    }

    public void setUsernameToGetPubKey(String usernameToGetPubKey) {
        this.usernameToGetPubKey = usernameToGetPubKey;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

}
