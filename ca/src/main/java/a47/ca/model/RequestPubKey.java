package a47.ca.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.PublicKey;

public class PublishPubKey {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private PublicKey publicKey;

    public String getUsername() {
        return username;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

}
