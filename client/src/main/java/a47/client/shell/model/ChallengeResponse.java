package a47.client.shell.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ChallengeResponse {
    private UUID uuid;

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotEmpty
    private byte[] unCipheredChallenge;

    public String getUsername() {
        return username;
    }

    public byte[] getUnCipheredChallenge() {
        return unCipheredChallenge;
    }

    public UUID getUUID() {
        return uuid;
    }
}
