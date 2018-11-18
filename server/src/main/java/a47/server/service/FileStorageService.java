package a47.server.service;

import a47.server.exception.ErrorMessage;
import a47.server.exception.FileAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileStorageService {

    public void uploadFile(MultipartFile newFile){
        File file = new File(newFile.getName());
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
