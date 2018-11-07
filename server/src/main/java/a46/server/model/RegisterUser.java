package a46.server.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RegisterUser {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
