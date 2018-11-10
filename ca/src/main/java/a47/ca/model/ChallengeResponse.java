package a47.ca.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ChallengeResponse {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private byte[] unCipheredChallenge;

    public String getUsername() {
        return username;
    }

    public byte[] getUnCipheredChallenge() {
        return unCipheredChallenge;
    }

}
