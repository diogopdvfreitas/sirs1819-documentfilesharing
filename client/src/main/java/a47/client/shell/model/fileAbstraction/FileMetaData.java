package a47.client.shell.model.fileAbstraction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class FileMetaData {
    private String owner;

    @NotNull
    @NotBlank
    private String fileName;

    FileMetaData(String owner, String fileName) {
        this.owner = owner;
        this.fileName = fileName;
    }

    public String getOwner() {
        return owner;
    }

    public String getFileName() {
        return fileName;
    }
}
