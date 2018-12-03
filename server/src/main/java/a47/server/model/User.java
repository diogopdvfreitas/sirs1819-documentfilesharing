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

    @JsonIgnore
    private PasswordHash passwordHash;

    public User(@NotNull @NotBlank String username, PasswordHash passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
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
}
