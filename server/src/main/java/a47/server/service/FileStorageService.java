package a47.server.service;

import a47.server.exception.ErrorMessage;
import a47.server.exception.FileAlreadyExistsException;
import a47.server.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileStorageService {
    private final String storedFilesFolder = "stored-files";
    @PostConstruct
    public void init() {

        File folder = new File(storedFilesFolder);
        if(!folder.exists()) {
            if (!folder.mkdir())
                throw new RuntimeException("Couldn't create folder for storing files");
            System.out.println("Creating stored files folder");
        }
    }

    public void uploadFile(String username, MultipartFile newFile){
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
    }
}
