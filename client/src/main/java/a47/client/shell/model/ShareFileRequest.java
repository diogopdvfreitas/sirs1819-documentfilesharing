package a47.client.shell.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ShareFileRequest {
    @NotNull
    @NotBlank
    private String targetUsername;

    @NotNull
    @NotBlank
    private String fileId;

    @NotNull
    private byte[] fileKey;

    public ShareFileRequest(@NotNull @NotBlank String targetUsername, @NotNull @NotBlank String fileId, @NotNull byte[] fileKey) {
        this.targetUsername = targetUsername;
        this.fileId = fileId;
        this.fileKey = fileKey;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getFileId() {
        return fileId;
    }

    public byte[] getFileKey() {
        return fileKey;
    }
}
