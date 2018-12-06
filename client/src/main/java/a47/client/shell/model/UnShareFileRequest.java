package a47.client.shell.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UnShareFileRequest {
    @NotNull
    @NotBlank
    private String targetUsername;

    @NotNull
    @NotBlank
    private String fileId;

    public UnShareFileRequest(@NotNull @NotBlank String targetUsername, @NotNull @NotBlank String fileId) {
        this.targetUsername = targetUsername;
        this.fileId = fileId;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getFileId() {
        return fileId;
    }
}
