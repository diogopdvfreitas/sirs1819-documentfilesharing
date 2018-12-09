package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.UploadFile;
import a47.client.shell.model.fileAbstraction.File;
import org.jboss.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
        byte[] cipheredHashFile = AuxMethods.cipherWithKS(filePlusHash, ks);
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
        String fileID = null;
        try {
            fileID = restTemplate.postForObject(Constants.FILE.UPLOAD_FILE_SERVER_URL, httpEntity, String.class);
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                ClientShell.setValidToken(false);
                return null;
            }
        }
        if (!fileID.isEmpty())
            return fileID;
        return null;
    }

    private byte[] generateKs(){
        return AuxMethods.generateKey();
    }
}