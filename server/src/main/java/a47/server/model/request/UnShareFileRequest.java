package a47.server.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UnShareFileRequest {
    @NotNull
    @NotBlank
    private String targetUsername;

    @NotNull
    @NotBlank
    private String fileId;

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getFileId() {
        return fileId;
    }
}
