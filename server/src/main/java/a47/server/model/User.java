package a47.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class User {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private String password;

    private PasswordHash passwordHash;

    @JsonIgnore
    private int loginTries;

    public User(@NotNull @NotBlank String username, PasswordHash passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.loginTries = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public int getLoginTries() {
        return loginTries;
    }

    public void setZeroLoginTries() {
        this.loginTries = 0;
    }

    public void incLoginTries() {
        this.loginTries++;
    }
}
