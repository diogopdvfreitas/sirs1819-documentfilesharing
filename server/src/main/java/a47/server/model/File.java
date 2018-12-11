package a47.server.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class File implements Serializable {
    @NotNull
    private byte[] content; //byte or Multipart file?

    private FileMetaData fileMetaData;

    public File(String fileName, String id, byte[] content, String owner) {
        this.content = content;
        this.fileMetaData = new FileMetaData(id, owner, fileName);
    }

    public File(byte[] content, FileMetaData fileMetaData) {
        this.content = content;
        this.fileMetaData = fileMetaData;
    }

    public File() {
    }

    public byte[] getContent() {
        return content;
    }

    public FileMetaData getFileMetaData() {
        return fileMetaData;
    }
}
