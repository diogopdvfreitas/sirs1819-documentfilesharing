package a47.server.model.response;

public class UserFileResponse {
    private String fileId;
    private String fileName;
    private String fileOwner;
    private String lastMod;

    public UserFileResponse(String fileId, String fileName, String fileOwner, String lastMod){
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileOwner = fileOwner;
        this.lastMod = lastMod;
    }

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
