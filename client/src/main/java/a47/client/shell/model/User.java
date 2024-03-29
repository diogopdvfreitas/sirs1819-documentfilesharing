package a47.client.shell.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class User {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private String password;

    public User(@NotNull @NotBlank String username, @NotNull @NotBlank String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
