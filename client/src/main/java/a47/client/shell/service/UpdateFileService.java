package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.ListAccessUserFilesRequest;
import a47.client.shell.model.UpdateFileRequest;
import a47.client.shell.model.fileAbstraction.File;
import a47.client.shell.model.fileAbstraction.FileMetaData;
import org.jboss.logging.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.SerializationUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Set;

public class UpdateFileService {
    private static Logger logger = Logger.getLogger(UpdateFileService.class);

    public Boolean UpdateFile(String fileId, String pathFile, long token) throws InvalidKeySpecException, NoSuchAlgorithmException {
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

        //Get Public Key from all users
        Set<String> usersToCipher = getListAccess(token, fileId);
        HashMap<String, byte[]> fileKey = new HashMap<>();

        //Sign KS with Public Key from user
        for(String user : usersToCipher){
            PublicKey publicKeyUser = AuxMethods.getPublicKeyFrom(user);
            fileKey.put(user, AuxMethods.sign(ks, publicKeyUser));
        }

        FileMetaData fileMetaData = (FileMetaData) SerializationUtils.deserialize(AuxMethods.getFile(pathFile + ".metadata"));
        UpdateFileRequest updateFileRequest = new UpdateFileRequest(new File(fileMetaData, cipheredHashFile), fileKey);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        HttpEntity<?> httpEntity = new HttpEntity<Object>(updateFileRequest, headers);
        FileMetaData newFileMetaData = restTemplate.postForObject(Constants.FILE.UPDATE_FILE_SERVER_URL, httpEntity, FileMetaData.class);
        if(newFileMetaData!= null){
            AuxMethods.saveFile(pathFile + ".metadata", SerializationUtils.serialize(newFileMetaData));
            return true;
        }
        return false;
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

    private Set<String> getListAccess(long token, String fileID) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        ListAccessUserFilesRequest listAccessUserFilesRequest = new ListAccessUserFilesRequest(fileID);
        HttpEntity<?> entity = new HttpEntity<Object>(listAccessUserFilesRequest, headers);

        ResponseEntity<Set<String>> response = restTemplate.exchange(
                Constants.FILE.GETACCESS_FILE_SERVER_URL,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Set<String>>(){});
        return response.getBody();
    }
}