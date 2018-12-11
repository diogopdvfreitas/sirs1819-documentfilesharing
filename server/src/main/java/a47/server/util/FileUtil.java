package a47.server.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static byte[] getFile(String pathFile){
        try {
            Path path = Paths.get(pathFile);
            return Files.readAllBytes(path);
        } catch (IOException | InvalidPathException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static Path saveFile(String pathFile, byte[] file){
        try {
            Path path = Paths.get(pathFile);
            Files.createDirectories(path.getParent());
            return Files.write(path, file);

        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }
}
