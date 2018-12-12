package a47.server.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static byte[] getFile(String pathFile){
        try {
            Path path = Paths.get(pathFile);
            return Files.readAllBytes(path);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static Path saveFile(String pathFile, byte[] file){
        try {
            Path path = Paths.get(pathFile);
            Files.createDirectories(path.getParent());
            return Files.write(path, file);

        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
}
