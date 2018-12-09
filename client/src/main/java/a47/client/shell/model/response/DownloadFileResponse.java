package a47.client.shell.model.response;

import a47.client.shell.model.fileAbstraction.File;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class DownloadFileResponse implements Serializable {
    @NotNull
    private File file;

    @NotNull
    private byte[] fileKey;

    public DownloadFileResponse() {
    }

    public DownloadFileResponse(@NotNull File file, @NotNull byte[] fileKey) {
        this.file = file;
        this.fileKey = fileKey;
    }

    public File getFile() {
        return file;
    }

    public byte[] getFileKey() {
        return fileKey;
    }
}
