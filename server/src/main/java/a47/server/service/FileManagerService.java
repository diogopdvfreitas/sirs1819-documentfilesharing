package a47.server.service;

import a47.server.exception.AccessDeniedException;
import a47.server.exception.ErrorMessage;
import a47.server.exception.FileNotFoundException;
import a47.server.exception.ServerException;
import a47.server.model.File;
import a47.server.model.FileMetaData;
import a47.server.model.request.UpdateFileRequest;
import a47.server.model.request.UploadFileRequest;
import a47.server.model.response.UserFileResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.util.*;

@Service
public class FileManagerService {
    private static Logger logger = Logger.getLogger(FileStorageService.class);

    private FileStorageService fileStorageService;

    private HashMap<String, List<String>> userFiles; //HashMap<username, List<fileId>>

    private HashMap<String, FileMetaData> filesMetaData;

    private HashMap<String, byte[]> fileHashes; //HashMap<fileId, hashed file content>


    public FileManagerService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.userFiles = new HashMap<>();
        this.filesMetaData = new HashMap<>();
        this.fileHashes = new HashMap<>();
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
        fileStorageService.saveFile(file);
        generateFileHashes(file);
        return fileId;
    }

    public FileMetaData updateFile(String username, UpdateFileRequest updateFileRequest){
        String fileId = updateFileRequest.getFile().getFileMetaData().getFileId();
        checkAccessPermission(username, fileId);
        if(!checkLastVersion(username, updateFileRequest.getFile().getFileMetaData())) {
            File file = new File(updateFileRequest.getFile().getContent(), filesMetaData.get(fileId));
            file.getFileMetaData().setLastModifiedBy(username);
            file.getFileMetaData().setUserKeys(updateFileRequest.getFileKey());
            file.getFileMetaData().setNewVersion();
            filesMetaData.put(fileId, file.getFileMetaData());
            fileStorageService.saveFile(file);
            generateFileHashes(file);
            return file.getFileMetaData();
        }
        return null;// TODO check this
    }

    public UploadFileRequest downloadFile(String username, String fileId){
        checkAccessPermission(username, fileId);
        File file = fileStorageService.getFile(fileId);
        if(file == null)
            throw new ServerException(ErrorMessage.CODE_SERVER_GENERAL, "File not found");
        FileMetaData fileMetaData = filesMetaData.get(fileId);
        return new UploadFileRequest(new File(file.getContent(), fileMetaData), fileMetaData.getUserKeys().get(username));
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
        return filesMetaData.get(fileMetaData.getFileId()).getVersion() != fileMetaData.getVersion();
    }

    public FileMetaData getFileMetadata(String username, String fileId){
        checkAccessPermission(username, fileId);
        return filesMetaData.get(fileId);
    }

    public List<UserFileResponse> listUserFiles(String username){
        List<UserFileResponse> userFilesResponses = new ArrayList<>();
        List<String> userFileIds = userFiles.get(username);
        for (String fileId : userFileIds){
            userFilesResponses.add(new UserFileResponse(fileId, filesMetaData.get(fileId).getFileName(), filesMetaData.get(fileId).getOwner(), filesMetaData.get(fileId).getLastModifiedBy()));
        }
        return userFilesResponses;
    }

    public Set<String> listAccessUserFiles(String username, String fileId){
        checkAccessPermission(username, fileId);
        return filesMetaData.get(fileId).getUserKeys().keySet();
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

    private String generateFileId(){
        return UUID.randomUUID().toString();
    }

    Boolean checkFilesIntegrity(){
        logger.info("Checking file integrity in case of ransomware attack");
        for(String fileId : filesMetaData.keySet()) {
            File file = fileStorageService.getFile(fileId);
            byte[] hashedFileContent_Disk = generateHash(SerializationUtils.serialize(file));

            byte[] hashedFileContent_Mem = fileHashes.get(fileId);

            if(!Arrays.equals(hashedFileContent_Disk, hashedFileContent_Mem)) {
                logger.info("Server compromised! Altered file: " + fileId);
                return true;
            }
        }
        logger.info("File integrity check complete, server safe");
        return false;
    }

    void generateFileHashes(File file) {
        fileHashes.put(file.getFileMetaData().getFileId(), generateHash(SerializationUtils.serialize(file)));
    }

    private static byte[] generateHash(byte[] file) {
        return DigestUtils.sha512(file);
    }

    public HashMap<String, byte[]> getFileHashes() {
        return fileHashes;
    }

    public HashMap<String, FileMetaData> getFilesMetaData() {
        return filesMetaData;
    }
    public HashMap<String, List<String>> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(HashMap<String, List<String>> userFiles) {
        this.userFiles = userFiles;
    }

    public void setFilesMetaData(HashMap<String, FileMetaData> filesMetaData) {
        this.filesMetaData = filesMetaData;
    }

}
