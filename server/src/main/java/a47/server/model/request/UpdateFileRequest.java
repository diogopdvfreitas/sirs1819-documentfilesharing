package a47.server.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UpdateFileRequest {
    @NotNull
    @NotBlank
    private String fileId;

    @NotNull
    private byte[] content;

    public String getFileId() {
        return fileId;
    }

    public byte[] getContent() {
        return content;
    }
}
