package a47.client.shell.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RequestPubKey {

    @NotNull
    @NotBlank
    private String usernameToGetPubKey;

    public RequestPubKey(@NotNull @NotBlank String usernameToGetPubKey) {
        this.usernameToGetPubKey = usernameToGetPubKey;
    }


    public String getUsernameToGetPubKey() {
        return usernameToGetPubKey;
    }
}
