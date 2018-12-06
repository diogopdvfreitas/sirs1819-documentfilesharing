package a47.client.shell.model.response;

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

    public ChallengeResponse(@NotNull UUID uuid, @NotNull @NotBlank String username, @NotNull @NotEmpty byte[] unCipheredChallenge) {
        this.uuid = uuid;
        this.username = username;
        this.unCipheredChallenge = unCipheredChallenge;
    }

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
