package a47.client.shell.model.fileAbstraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

public class FileMetaData {

    private String fileId;

    private String owner;

    private String lastModifiedBy;

    @NotNull
    @NotBlank
    private String fileName;

    //keys to have access to the file
    @JsonIgnore
    private HashMap<String, byte[]> userKeys;

    FileMetaData() {

    }

    FileMetaData(String owner, String fileName) {
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public HashMap<String, byte[]> getUserKeys() {
        return userKeys;
    }
}
