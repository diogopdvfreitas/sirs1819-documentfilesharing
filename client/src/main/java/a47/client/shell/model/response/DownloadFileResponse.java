package a47.client.shell.model.response;

import a47.client.shell.model.fileAbstraction.File;

import javax.validation.constraints.NotNull;

public class DownloadFileResponse {
    @NotNull
    private File file;

    @NotNull
    private byte[] fileKey;

    public File getFile() {
        return file;
    }

    public byte[] getFileKey() {
        return fileKey;
    }
}
