package a47.server.service;

import a47.server.model.File;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileStorageService {
    private static Logger logger = Logger.getLogger(FileStorageService.class);
    private final String FILES_DIRECTORY = "/var/project-sirs/server";

    void saveFile(File file){
        try {
            FileOutputStream fileOut = new FileOutputStream(FILES_DIRECTORY + "/" + file.getFileMetaData().getFileId());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(file);
            out.close();
            fileOut.close();
            logger.info("File metadata " + file.getFileMetaData().getFileName() + " saved on disk");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    File getFile(String fileName){
        File file = null;
        try {
            FileInputStream fileIn = new FileInputStream(FILES_DIRECTORY + "/" + fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            file = (File) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }
        return file;
    }
}
