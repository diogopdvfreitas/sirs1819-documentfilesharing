package a47.server.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ListAccessUserFilesRequest {
    @NotNull
    @NotBlank
    private String fileId;

    public String getFileId() {
        return fileId;
    }
}
