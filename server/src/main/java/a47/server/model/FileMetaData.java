package a47.server.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FileMetaData {

    private String fileId;

    private String owner;

    @NotNull
    @NotBlank
    private String fileName;

    //keys to have access to the file
    //private HashMap<String, String> userKeys;

    FileMetaData(String fileId, String owner, String fileName) {
        this.fileId = fileId;
        this.owner = owner;
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public String getOwner() {
        return owner;
    }

    public String getFileName() {
        return fileName;
    }
}
