package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.UploadFile;
import a47.client.shell.model.fileAbstraction.File;
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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class UploadFileService {
    private static Logger logger = Logger.getLogger(UploadFileService.class);

    public String UploadFile(String nameFile, String pathFile, long token) {
        //GENERATE KS
        byte[] ks = generateKs();
        //GET FILE
        byte[] file = AuxMethods.getFile(pathFile);
        if (file == null) {
            logger.error("Get file");
            return null;
        }
        //GENERATE HASH
        byte[] hashFile = AuxMethods.generateHash(file);
        //SIGN HASH WITH PRIVATE KEY
        byte[] hashSigned = AuxMethods.sign(hashFile, ClientShell.keyManager.getPrivateKey());
        if (hashSigned == null) {
            logger.error("Signing hash");
            return null;
        }

        byte[] filePlusHash = AuxMethods.concatenateByteArray(file, hashSigned);
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
        byte[] ksSigned = AuxMethods.sign(ks, ClientShell.keyManager.getPublicKey());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        UploadFile uploadFile = new UploadFile(new File(nameFile, cipheredHashFile), ksSigned);
        HttpEntity<?> httpEntity = new HttpEntity<Object>(uploadFile, headers);
        String fileID = restTemplate.postForObject(Constants.FILE.UPLOAD_FILE_SERVER_URL, httpEntity, String.class);
        if (!fileID.isEmpty())
            return fileID;
        return null;
    }

    private byte[] generateKs(){
        return AuxMethods.generateKey();
    }

    private byte[] cipherWithKS(byte[] bytes, byte[] ks) {
        try {
            // Generating IV.
            byte[] iv = new byte[Constants.FILE.IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Encrypt.
            Cipher cipher = Cipher.getInstance(Constants.FILE.SYMMETRIC_ALGORITHM_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(ks, Constants.FILE.SYMMETRIC_ALGORITHM), ivParameterSpec);
            byte[] encrypted = cipher.doFinal(bytes);

            // Combine IV and encrypted part.
            return AuxMethods.concatenateByteArray(iv, encrypted);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            //e.printStackTrace();
            return null;
        }
    }
}