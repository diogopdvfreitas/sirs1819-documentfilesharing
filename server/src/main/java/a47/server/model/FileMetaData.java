package a47.server.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.HashMap;

public class FileMetaData implements Serializable {

    private String fileId;

    private String owner;

    private String lastModifiedBy;

    private long version;

    @NotNull
    @NotBlank
    private String fileName;

    //keys to have access to the file
    private HashMap<String, byte[]> userKeys;

    FileMetaData(String fileId, String owner, String fileName) {
        this.fileId = fileId;
        this.owner = owner;
        this.fileName = fileName;
        this.lastModifiedBy = owner;
        this.userKeys = new HashMap<>();
        this.version = new SecureRandom().nextLong();
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

    public long getVersion() { return version; }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void setNewVersion() {
        this.version = new SecureRandom().nextLong();
    }

    public HashMap<String, byte[]> getUserKeys() {
        return userKeys;
    }

    public void setUserKeys(HashMap<String, byte[]> userKeys) {
        this.userKeys = userKeys;
    }
}
