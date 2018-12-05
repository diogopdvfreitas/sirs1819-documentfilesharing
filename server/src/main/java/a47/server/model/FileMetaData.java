package a47.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;

public class FileMetaData implements Serializable {

    private String fileId;

    private String owner;

    private String lastModifiedBy;

    @NotNull
    @NotBlank
    private String fileName;

    //keys to have access to the file
    @JsonIgnore
    private HashMap<String, byte[]> userKeys;

    FileMetaData(String fileId, String owner, String fileName) {
        this.fileId = fileId;
        this.owner = owner;
        this.fileName = fileName;
        this.lastModifiedBy = owner;
        this.userKeys = new HashMap<>();
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
