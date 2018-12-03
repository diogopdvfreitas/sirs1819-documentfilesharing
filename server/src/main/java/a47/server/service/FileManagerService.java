package a47.server.service;

import a47.server.exception.ErrorMessage;
import a47.server.exception.InvalidUserOrPassException;
import a47.server.model.File;
import org.apache.commons.codec.binary.Base64;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FileManagerService {
    private static Logger logger = Logger.getLogger(FileManagerService.class);

    private FileStorageService fileStorageService;

    private HashMap<String, List<String>> userFiles;

    public FileManagerService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.userFiles = new HashMap<>();
    }

    /*private final String storedFilesFolder = "stored-files";
    @PostConstruct
    public void init() {

        File folder = new File(storedFilesFolder);
        if(!folder.exists()) {
            if (!folder.mkdir())
                throw new RuntimeException("Couldn't create folder for storing files");
            System.out.println("Creating stored files folder");
        }
    }*/

    void addUser(String username){
        userFiles.put(username, new ArrayList<>());
    }

    public String uploadFile(String username, File newFile){// returns unique file id
        String fileId = generateFileId();
        File file = new File(newFile.getFilename(), fileId, newFile.getContent(), username);
        userFiles.get(username).add(fileId);
        logger.debug("File content: " + Base64.encodeBase64String(file.getContent()));
        fileStorageService.saveFile(fileId, file);
        return fileId;
    }

    public File downloadFile(String username, String fileId){
        if(!userFiles.get(username).contains(fileId))
            throw new InvalidUserOrPassException(ErrorMessage.CODE_SERVER_INV_USER, "Access Denied!"); //TODO make exception of access denied
        byte[] content = fileStorageService.getFile(fileId);
        return new File("cenas", fileId, content, "dunno"); //TODO where store the metadata
    }


    private String generateFileId(){
        return UUID.randomUUID().toString();
    }

    /*public void uploadFile(String username, MultipartFile newFile){
        String fileFolder = this.storedFilesFolder + "/" + username;
        String fileName = StringUtils.cleanPath(newFile.getOriginalFilename());
        new File(fileFolder).mkdirs();
        File file = new File( fileFolder + "/" + fileName);
        try {
            if(!file.createNewFile())
                throw new FileAlreadyExistsException(ErrorMessage.CODE_SERVER_DUP_FILE, "File already exists");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(newFile.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
