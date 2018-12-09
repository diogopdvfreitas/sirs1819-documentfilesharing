package a47.client.shell.model.fileAbstraction;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class File implements Serializable {
    @NotNull
    private byte[] content; //byte or Multipart file?

    private FileMetaData fileMetaData;

    File() {
    }

    public File(String fileName, byte[] content) {
        this.content = content;
        this.fileMetaData = new FileMetaData(fileName);
    }

    public File(FileMetaData fileMetaData, byte[] content) {
        this.content = content;
        this.fileMetaData = fileMetaData;
    }

    public byte[] getContent() {
        return content;
    }

    public FileMetaData getFileMetaData() {
        return fileMetaData;
    }
}
