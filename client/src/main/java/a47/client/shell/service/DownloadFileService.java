package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.DownloadFile;
import a47.client.shell.model.response.DownloadFileResponse;
import org.jboss.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class DownloadFileService {
    private static Logger logger = Logger.getLogger(DownloadFileService.class);

    public Path downloadFile(String username, String pathToStore, String fileId, long token){
        DownloadFileResponse file = requestToServer(token, fileId);
        if(file == null)
            return null;
        //Decipher KS
        byte[] ks = AuxMethods.unSign(file.getFileKey(), ClientShell.keyManager.getPrivateKey());
        if (ks == null) {
            logger.error("Unsign KS");
            return null;
        }
        //Decipher file message
        byte[] filePlusCipheredHash = decipherWithKS(file.getFile().getContent(), ks);
        if (filePlusCipheredHash == null) {
            logger.error("Deciphering file message");
            return null;
        }

        //Extract file
        int fileSize = filePlusCipheredHash.length - Constants.FILE.CIPHERED_HASH_SIZE;
        byte[] fileBytes = new byte[fileSize];
        System.arraycopy(filePlusCipheredHash, 0, fileBytes, 0, fileSize);

        //Extract ciphered hash
        byte[] cipheredHash = new byte[Constants.FILE.CIPHERED_HASH_SIZE];
        System.arraycopy(filePlusCipheredHash, filePlusCipheredHash.length - Constants.FILE.CIPHERED_HASH_SIZE, cipheredHash, 0, Constants.FILE.CIPHERED_HASH_SIZE);

        //Generate Hash of downloaded file
        byte[] hash = AuxMethods.generateHash(fileBytes);
        //Get publicKey from CA of Last Modif.
        PublicKey publicKeyUploader;
        try {
            publicKeyUploader = AuxMethods.getPublicKeyFrom(username, file.getFile().getFileMetaData().getLastModifiedBy());
        } catch (InvalidKeySpecException| NoSuchAlgorithmException e) {
            logger.error("Get Public Key From Last Modif.");
            return null;
        }
        //Deciphered Hash
        byte[] decipheredHash = AuxMethods.decipherWithPrivateKey(cipheredHash, publicKeyUploader);
        if (decipheredHash == null) {
            logger.error("Deciphering hash");
            return null;
        }
        if(Arrays.equals(decipheredHash, hash))
            return saveFile(pathToStore + file.getFile().getFileMetaData().getFileName(), fileBytes);
        else{
            logger.error("File was modified");
            return null;
        }
    }

    private DownloadFileResponse requestToServer(long token, String fileId){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        DownloadFileResponse downloadFileResponse = null;
        try {
            DownloadFile downloadFile = new DownloadFile(fileId);
            HttpEntity<?> httpEntity = new HttpEntity<Object>(downloadFile, headers);
            downloadFileResponse = restTemplate.postForObject(Constants.FILE.DOWNLOAD_FILE_SERVER_URL, httpEntity, DownloadFileResponse.class);
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED)
                logger.info("Unauthorized");
            else if(e.getStatusCode() == HttpStatus.NOT_FOUND)
                logger.info("File not found");
            else if(e.getStatusCode() == HttpStatus.FORBIDDEN)
                logger.info("Access denied to file");
        }
        return downloadFileResponse;
    }

    private byte[] decipherWithKS(byte[] bytes, byte[] ks) {
        try {
            // Extract IV.
            byte[] iv = new byte[Constants.FILE.IV_SIZE];
            System.arraycopy(bytes, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Extract encrypted part.
            int encryptedSize = bytes.length - Constants.FILE.IV_SIZE;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(bytes, Constants.FILE.IV_SIZE, encryptedBytes, 0, encryptedSize);

            // Decrypt.
            Cipher cipherDecrypt;
            cipherDecrypt = Cipher.getInstance(Constants.FILE.SYMMETRIC_ALGORITHM_MODE);
            cipherDecrypt.init(Cipher.DECRYPT_MODE, new SecretKeySpec(ks, Constants.FILE.SYMMETRIC_ALGORITHM), ivParameterSpec);
            return cipherDecrypt.doFinal(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Path saveFile(String pathFile, byte[] file){
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


