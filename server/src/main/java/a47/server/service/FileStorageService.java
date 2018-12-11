package a47.server.service;

import a47.server.Constants;
import a47.server.model.File;
import a47.server.util.FileUtil;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

@Service
public class FileStorageService {
    private static Logger logger = Logger.getLogger(FileStorageService.class);

    void saveFile(File file){
        try {
            FileUtil.saveFile(Constants.DIRECTORY.FILES_DIRECTORY + "/" + file.getFileMetaData().getFileId(), SerializationUtils.serialize(file));
            //logger.info("File " + file.getFileMetaData().getFileId() + " saved on disk");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    File getFile(String fileName){
        File file = null;
        try {
            file = (File) SerializationUtils.deserialize(FileUtil.getFile(Constants.DIRECTORY.FILES_DIRECTORY + "/" + fileName));
        } catch (Exception i) {
            i.printStackTrace();
        }
        return file;
    }
}

