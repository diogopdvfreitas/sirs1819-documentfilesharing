package a47.server.service;

import a47.server.exception.AccessDeniedException;
import a47.server.exception.ErrorMessage;
import a47.server.model.File;
import a47.server.model.FileMetaData;
import a47.server.model.request.UploadFileRequest;
import a47.server.model.response.UserFileResponse;
import org.apache.commons.codec.binary.Base64;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileManagerService {
    private static Logger logger = Logger.getLogger(FileStorageService.class);

    private FileStorageService fileStorageService;

    private HashMap<String, List<String>> userFiles; //HashMap<username, List<fileId>>

    private HashMap<String, FileMetaData> filesMetaData;

    public FileManagerService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.userFiles = new HashMap<>();
        this.filesMetaData = new HashMap<>();
    }

    void addUser(String username){
        userFiles.put(username, new ArrayList<>());
    }

    public String uploadFile(String username, File newFile, byte[] fileKey){// returns unique file id
        String fileId = generateFileId();
        File file = new File(newFile.getFileMetaData().getFileName(), fileId, newFile.getContent(), username);
        userFiles.get(username).add(fileId);
        file.getFileMetaData().getUserKeys().put(username, fileKey);
        filesMetaData.put(fileId, file.getFileMetaData());
        logger.debug("File content: " + Base64.encodeBase64String(file.getContent()));
        fileStorageService.saveFile(fileId, file);
        fileStorageService.saveFileMetada(file.getFileMetaData());
        return fileId;
    }

    public UploadFileRequest downloadFile(String username, String fileId){
        if(!userFiles.get(username).contains(fileId))
            throw new AccessDeniedException(ErrorMessage.CODE_SERVER_ACCESS_DENIED, "Access Denied!");
        byte[] content = fileStorageService.getFile(fileId);
        FileMetaData fileMetaData = filesMetaData.get(fileId);//TODO REPLACE this to fetch from disk using fileStorageService.getFileMetadata
        return new UploadFileRequest(new File(fileMetaData.getFileName(), fileId, content, fileMetaData.getOwner()), fileMetaData.getUserKeys().get(username)); //TODO where store the metadata
    }

    public void shareFile(String username, String targetUsername, String filedId, byte[] fileKey){
        if (CheckFilePermissions(username, targetUsername, filedId)) return;
        userFiles.get(targetUsername).add(filedId);
        filesMetaData.get(filedId).getUserKeys().put(targetUsername, fileKey);
    }

    public void unShareFile(String username, String targetUsername, String filedId){
        if (CheckFilePermissions(username, targetUsername, filedId)) return;
        userFiles.get(targetUsername).remove(filedId);
        filesMetaData.get(filedId).getUserKeys().remove(username);
    }

    private boolean CheckFilePermissions(String username, String targetUsername, String filedId) {
        if(!userFiles.get(username).contains(filedId) || !filesMetaData.get(filedId).getOwner().equals(username))
            throw new AccessDeniedException(ErrorMessage.CODE_SERVER_ACCESS_DENIED, "Access Denied: User don't have access to file or its not his owner");
        //Cannot share with himself
        return username.equals(targetUsername);
    }

    public List<UserFileResponse> listUserFiles(String username){
        List<UserFileResponse> userFilesResponses = new ArrayList<UserFileResponse>();
        List<String> userFileIds = userFiles.get(username);
        for (String fileId : userFileIds){
            userFilesResponses.add(new UserFileResponse(fileId, filesMetaData.get(fileId).getFileName(), filesMetaData.get(fileId).getOwner()));
        }
        return userFilesResponses;
    }

    private String generateFileId(){
        return UUID.randomUUID().toString();
    }
}
