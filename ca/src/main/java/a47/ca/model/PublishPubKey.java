package a47.ca.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PublishPubKey {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    //private PublicKey publicKey;
    private String publicKey;

    public String getUsername() {
        return username;
    }

//    public PublicKey getPublicKey() {
//        return publicKey;
//    }
    public String getPublicKey() {
    return publicKey;
}
}
