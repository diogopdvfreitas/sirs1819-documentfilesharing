package a47.client.shell.model.fileAbstraction;

import javax.validation.constraints.NotNull;

public class File {
    @NotNull
    private byte[] content; //byte or Multipart file?

    private FileMetaData fileMetaData;

    public File(String fileName, byte[] content, String owner) {
        this.content = content;
        this.fileMetaData = new FileMetaData(owner, fileName);
    }

    public byte[] getContent() {
        return content;
    }

    public FileMetaData getFileMetaData() {
        return fileMetaData;
    }
}
