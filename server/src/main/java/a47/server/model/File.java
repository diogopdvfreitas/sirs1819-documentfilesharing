package a47.server.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class File {
    @NotNull
    @NotBlank
    private String filename;

    private String id;

    @NotNull
    private byte[] content; //byte or Multipart file?

    private String owner;
    //keys to have access to the file
    //private HashMap<String, String> userKeys;


    public File(String filename, String id, byte[] content, String onwer) {
        this.filename = filename;
        this.id = id;
        this.content = content;
        this.owner = onwer;
    }

    public String getFilename() {
        return filename;
    }

    public String getId() {
        return id;
    }

    public byte[] getContent() {
        return content;
    }

    public String getOwner() {
        return owner;
    }
}
