package a47.client.shell.model.response;

public class UserFileResponse {
    private String fileId;
    private String fileName;
    private String fileOwner;
    private String lastMod;

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileOwner() {
        return fileOwner;
    }

    public String getLastMod() {
        return lastMod;
    }
}
