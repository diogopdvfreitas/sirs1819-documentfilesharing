package a47.client.shell.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PublishPubKey {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotEmpty
    private byte[] publicKey;

    public PublishPubKey(@NotNull @NotBlank String username, @NotNull @NotEmpty byte[] publicKey) {
        this.username = username;
        this.publicKey = publicKey;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

}
