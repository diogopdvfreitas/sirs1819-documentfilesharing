package a47.server.service;

import a47.server.exception.AccessDeniedException;
import a47.server.exception.ErrorMessage;
import a47.server.exception.FileNotFoundException;
import a47.server.model.File;
import a47.server.model.FileMetaData;
import a47.server.model.request.UploadFileRequest;
import a47.server.model.response.UserFileResponse;
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
        file.getFileMetaData().getUserKeys().put(username, fileKey);
        userFiles.get(username).add(fileId);
        filesMetaData.put(fileId, file.getFileMetaData());
        fileStorageService.saveFile(fileId, file);
        fileStorageService.saveFileMetada(file.getFileMetaData());
        return fileId;
    }

    public void updateFile(String username, String fileId, byte[] content){
        checkAccessPermission(username, fileId);
        File file = new File(content, filesMetaData.get(fileId));
        file.getFileMetaData().setLastModifiedBy(username);
        filesMetaData.put(fileId, file.getFileMetaData());
        fileStorageService.saveFile(file.getFileMetaData().getFileId(), file);
        fileStorageService.saveFileMetada(file.getFileMetaData());
    }

    public UploadFileRequest downloadFile(String username, String fileId){
        checkAccessPermission(username, fileId);
        byte[] content = fileStorageService.getFile(fileId);
        FileMetaData fileMetaData = filesMetaData.get(fileId);//TODO REPLACE this to fetch from disk using fileStorageService.getFileMetadata
        return new UploadFileRequest(new File(content, fileMetaData), fileMetaData.getUserKeys().get(username)); //TODO where store the metadata
    }

    public void shareFile(String username, String targetUsername, String filedId, byte[] fileKey){
        if (checkSharePermissions(username, targetUsername, filedId)) return;
        if(!userFiles.get(targetUsername).contains(filedId))//TODO is it supposed to return a error saying that he already have access to the file
            userFiles.get(targetUsername).add(filedId);
        filesMetaData.get(filedId).getUserKeys().put(targetUsername, fileKey);
    }

    public void unShareFile(String username, String targetUsername, String filedId){
        if (checkSharePermissions(username, targetUsername, filedId)) return;
        userFiles.get(targetUsername).remove(filedId);//TODO is it supposed to return a error saying that targetusername already dont have acces to the file
        filesMetaData.get(filedId).getUserKeys().remove(username);
    }

    public boolean checkLastVersion(String username, FileMetaData fileMetaData){
        if(!filesMetaData.containsKey(fileMetaData.getFileId()))
            throw new FileNotFoundException(ErrorMessage.CODE_SERVER_NOT_FOUND_FILE, "File not found");
        if(!userFiles.get(username).contains(fileMetaData.getFileId()))
            throw new AccessDeniedException(ErrorMessage.CODE_SERVER_ACCESS_DENIED, "Access Denied!");
        return filesMetaData.get(fileMetaData.getFileId()).getVersion() == fileMetaData.getVersion();
    }

    private void checkAccessPermission(String username, String fileId){
        if(!filesMetaData.containsKey(fileId))
            throw new FileNotFoundException(ErrorMessage.CODE_SERVER_NOT_FOUND_FILE, "File not found");
        if(!userFiles.get(username).contains(fileId))
            throw new AccessDeniedException(ErrorMessage.CODE_SERVER_ACCESS_DENIED, "Access Denied!");
    }

    private boolean checkSharePermissions(String username, String targetUsername, String filedId) {
        if(!userFiles.get(username).contains(filedId) || !filesMetaData.get(filedId).getOwner().equals(username))
            throw new AccessDeniedException(ErrorMessage.CODE_SERVER_ACCESS_DENIED, "Access Denied: User don't have access to file or its not his owner");
        //Cannot share with himself
        return username.equals(targetUsername);
    }

    public List<UserFileResponse> listUserFiles(String username){
        List<UserFileResponse> userFilesResponses = new ArrayList<>();
        List<String> userFileIds = userFiles.get(username);
        for (String fileId : userFileIds){
            userFilesResponses.add(new UserFileResponse(fileId, filesMetaData.get(fileId).getFileName(), filesMetaData.get(fileId).getOwner(), filesMetaData.get(fileId).getLastModifiedBy()));
        }
        return userFilesResponses;
    }

    private String generateFileId(){
        return UUID.randomUUID().toString();
    }
}
