package a47.client.shell.model.fileAbstraction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class FileMetaData implements Serializable {

    private String fileId;

    private String owner;

    private String lastModifiedBy;

    private long version;

    @NotNull
    @NotBlank
    private String fileName;

    FileMetaData() {

    }

    FileMetaData( String fileName) {
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

    public long getVersion() {
        return version;
    }
}
