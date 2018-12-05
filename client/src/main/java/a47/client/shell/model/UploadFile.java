package a47.client.shell.model;

import a47.client.shell.model.fileAbstraction.File;

import javax.validation.constraints.NotNull;

public class UploadFile {
    @NotNull
    private File file;

    @NotNull
    private byte[] fileKey;

    public UploadFile(File file, byte[] fileKey) {
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
