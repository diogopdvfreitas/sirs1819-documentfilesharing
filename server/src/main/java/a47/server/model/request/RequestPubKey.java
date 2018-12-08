package a47.server.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RequestPubKey {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private String usernameToGetPubKey;

    public RequestPubKey(@NotNull @NotBlank String username, @NotNull @NotBlank String usernameToGetPubKey) {
        this.username = username;
        this.usernameToGetPubKey = usernameToGetPubKey;
    }

    public String getUsername() {
        return username;
    }

    public String getUsernameToGetPubKey() {
        return usernameToGetPubKey;
    }
}
