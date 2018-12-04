package a47.server.service;

import a47.server.model.File;
import a47.server.model.FileMetaData;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileStorageService {
    private static Logger logger = Logger.getLogger(FileStorageService.class);
    private final String FILES_DIRECTORY = "/var/project-sirs/server";

    void saveFile(String fileName, File file){
        String fullPath = FILES_DIRECTORY + "/" + fileName;
        boolean mkdirs = new java.io.File(FILES_DIRECTORY).mkdirs();
        try {
            Files.write(Paths.get(fullPath), file.getContent(), StandardOpenOption.CREATE);
            logger.info("File " + fileName + " saved on disk");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    byte[] getFile(String fileName){
        String fullPath = FILES_DIRECTORY + "/" + fileName;
        byte[] file = new byte[0]; //TODO meter isto mais elegante
        try {
            file = Files.readAllBytes(Paths.get(fullPath));
            logger.info("File " + fileName + " fetched from disk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    void saveFileMetada(FileMetaData fileMetaData){
        try {
            FileOutputStream fileOut = new FileOutputStream(FILES_DIRECTORY + "/" + fileMetaData.getFileId() + ".metadata");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(fileMetaData);
            out.close();
            fileOut.close();
            logger.info("File metadata " + fileMetaData.getFileName() + " saved on disk");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    FileMetaData getFileMetadata(String fileId){//TODO check this
        FileMetaData fileMetaData = null;
        try {
            FileInputStream fileIn = new FileInputStream(FILES_DIRECTORY + "/" + fileId + ".metadata");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            fileMetaData = (FileMetaData) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }
        return fileMetaData;
    }
}
