package a47.client.shell.model;

import a47.client.shell.model.fileAbstraction.File;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

public class UpdateFileRequest {
    @NotNull
    private File file;

    @NotNull
    private HashMap<String, byte[]> fileKey;

    public UpdateFileRequest(@NotNull File file, @NotNull HashMap<String, byte[]> fileKey) {
        this.file = file;
        this.fileKey = fileKey;
    }

    public File getFile() {
        return file;
    }

    public HashMap<String, byte[]> getFileKey() {
        return fileKey;
    }
}
