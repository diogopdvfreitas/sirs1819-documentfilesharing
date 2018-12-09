package a47.ca.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RequestPubKey {
    @NotNull
    @NotBlank
    private String usernameToGetPubKey;

    public String getUsernameToGetPubKey() {
        return usernameToGetPubKey;
    }
}
