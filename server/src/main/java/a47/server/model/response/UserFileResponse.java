package a47.server.model.response;

public class UserFileResponse {
    private String fileId;
    private String fileName;
    private String fileOwner;

    public UserFileResponse(String fileId, String fileName, String fileOwner){
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileOwner = fileOwner;
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
}
