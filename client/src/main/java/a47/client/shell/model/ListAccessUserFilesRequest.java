package a47.client.shell.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ListAccessUserFilesRequest {
    @NotNull
    @NotBlank
    private String fileId;

    public ListAccessUserFilesRequest(@NotNull @NotBlank String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }
}
