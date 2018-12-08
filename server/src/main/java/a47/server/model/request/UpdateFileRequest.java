package a47.server.model.request;

import a47.server.model.File;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

public class UpdateFileRequest {
    @NotNull
    private File file;

    @NotNull
    private HashMap<String, byte[]> fileKey;

    public File getFile() {
        return file;
    }

    public HashMap<String, byte[]> getFileKey() {
        return fileKey;
    }
}
