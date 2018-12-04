package a47.server.model.request;

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
