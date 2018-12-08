package a47.server.model.request;

import a47.server.model.User;
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
    private byte[] challenge;

    private Date generatedDate;

    private User user;

    public Challenge() {
    }

    public Challenge(UUID uuid, @NotNull @NotBlank String username, @NotNull @NotEmpty byte[] challenge, Date generatedDate, User user) {
        this.uuid = uuid;
        this.username = username;
        this.challenge = challenge;
        this.generatedDate = generatedDate;
        this.user = user;
    }

    public Challenge(@NotNull @NotBlank String username, @NotNull @NotEmpty byte[] challenge, Date generatedDate) {
        this.uuid = UUID.randomUUID();
        this.username = username;
        this.challenge = challenge;
        this.generatedDate = generatedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getChallenge() {
        return challenge;
    }

    public Date getGeneratedDate() {
        return generatedDate;
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

    public void setChallenge(byte[] challenge) {
        this.challenge = challenge;
    }

    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }

}
