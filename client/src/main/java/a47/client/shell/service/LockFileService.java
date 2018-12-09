package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.fileAbstraction.File;
import a47.client.shell.model.fileAbstraction.FileMetaData;
import a47.client.shell.model.response.DownloadFileResponse;
import org.jboss.logging.Logger;
import org.springframework.util.SerializationUtils;

import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class LockFileService {
    private static Logger logger = Logger.getLogger(LockFileService.class);

    public Path UnlockFile(String pathToStore, String fileId, long token){
        DownloadFileResponse file = (DownloadFileResponse) SerializationUtils.deserialize(AuxMethods.getFile(pathToStore));
        if(file == null)
            return null;
        //Decipher KS
        byte[] ks = AuxMethods.unSign(file.getFileKey(), ClientShell.keyManager.getPrivateKey());
        if (ks == null) {
            logger.error("Unsign KS");
            return null;
        }
        //Decipher file message
        byte[] filePlusCipheredHash = AuxMethods.decipherWithKS(file.getFile().getContent(), ks);
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
            publicKeyUploader = AuxMethods.getPublicKeyFrom(file.getFile().getFileMetaData().getLastModifiedBy());
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
        if(Arrays.equals(decipheredHash, hash)){
            return AuxMethods.saveFile(pathToStore + "_Unlocked", fileBytes);
        }else{
            logger.error("File was modified");
            return null;
        }
    }

    public Boolean LockFile(String pathFile) {
        //GENERATE KS
        byte[] ks = generateKs();
        //GET FILE
        byte[] file = AuxMethods.getFile(pathFile + "_Unlocked");
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

        DownloadFileResponse filetoSave = new DownloadFileResponse(new File((FileMetaData) SerializationUtils.deserialize(AuxMethods.getFile(pathFile+".metadata")), cipheredHashFile), ksSigned);
        AuxMethods.saveFile(pathFile, SerializationUtils.serialize(filetoSave));
        AuxMethods.deleteFile(pathFile + "_Unlocked");
        return true;
    }

    private byte[] generateKs(){
        return AuxMethods.generateKey();
    }
}


