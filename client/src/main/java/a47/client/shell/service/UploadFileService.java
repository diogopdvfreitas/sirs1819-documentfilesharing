package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.UploadFile;
import a47.client.shell.model.fileAbstraction.File;
import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import java.security.*;

public class UploadFileService {
    private static Logger logger = Logger.getLogger(UploadFileService.class);

    public String UploadFile(String user, String nameFile, String pathFile, long token) {
        //GENERATE KS
        byte[] ks = generateKs();
        //GET FILE
        byte[] file = getFile(pathFile);
        if (file == null) {
            logger.error("Get file");
            return null;
        }
        //GENERATE HASH
        byte[] hashFile = generateHash(file);
        byte[] filePlusHash = AuxMethods.concatenateByteArray(file, hashFile);
        if (filePlusHash == null) {
            logger.error("Concatenate Arrays");
            return null;
        }
        //CIPHER FILE PLUS HASH WITH KS
        byte[] cipheredHashFile = cipherWithKS(filePlusHash, ks);
        if (cipheredHashFile == null) {
            logger.error("Cipher hash with KS");
            return null;
        }
        //Sign KS with Public Key from user
        byte[] ksSigned = sign(ks, ClientShell.keyManager.getPublicKey());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        UploadFile uploadFile = new UploadFile(new File(nameFile, cipheredHashFile, user), ksSigned);
        HttpEntity<?> httpEntity = new HttpEntity<Object>(uploadFile, headers);
        String fileID = restTemplate.postForObject(Constants.FILE.UPLOAD_FILE_SERVER_URL, httpEntity, String.class);
        if (!fileID.isEmpty())
            return fileID;
        return null;
    }

    private byte[] generateKs(){
        return AuxMethods.generateKey();
    }

    private byte[] getFile(String pathFile){
        try {
            Path path = Paths.get(pathFile);
            System.out.println(pathFile);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    private byte[] cipherWithKS(byte[] bytes, byte[] ks) {
        try {
            // Generating IV.
            int ivSize = 16;
            byte[] iv = new byte[ivSize];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Encrypt.
            Cipher cipher = Cipher.getInstance(Constants.FILE.SYMMETRIC_ALGORITHM_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(ks,Constants.FILE.SYMMETRIC_ALGORITHM), ivParameterSpec);
            byte[] encrypted = cipher.doFinal(bytes);

            // Combine IV and encrypted part.
            return AuxMethods.concatenateByteArray(iv, encrypted);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            //e.printStackTrace();
            return null;
        }
    }

    private byte[] generateHash(byte[] file) {
        return DigestUtils.sha512(file);
    }

    private byte[] sign(byte[] ks, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(ks);
        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e) {
            //e.printStackTrace();
            return null;
        }
    }

}