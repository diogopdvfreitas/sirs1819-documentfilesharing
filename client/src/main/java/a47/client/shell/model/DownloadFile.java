package a47.client.shell.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class DownloadFile {
    @NotNull
    @NotBlank
    private String fileId;

    public DownloadFile(@NotNull @NotBlank String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }
}
