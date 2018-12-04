package a47.server.model.request;

import a47.server.model.File;

import javax.validation.constraints.NotNull;

public class UploadFileRequest {
    @NotNull
    private File file;

    @NotNull
    private byte[] fileKey;

    public UploadFileRequest(File file, byte[] fileKey) {
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
