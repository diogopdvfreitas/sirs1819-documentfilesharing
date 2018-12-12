package a47.client;


import a47.client.shell.ClientShell;
import a47.client.shell.model.RequestPubKey;
import a47.client.shell.model.response.UserFileResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.logging.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class AuxMethods {
    private static Logger logger = Logger.getLogger(AuxMethods.class);

    public static byte[] decipherWithPrivateKey(byte[] cipheredData, Key privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(Constants.Keys.CA_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipheredData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            logger.info("Cant decipher with private key");
        }
        return null;
    }

    private static PublicKey decodePubKey(byte[] encodedPubKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(encodedPubKey);
        KeyFactory kf = KeyFactory.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
        return kf.generatePublic(ks);
    }

    private static PrivateKey decodePrivKey(byte[] encodedPrivKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(encodedPrivKey);
        KeyFactory kf = KeyFactory.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
        return kf.generatePrivate(ks);
    }

    public static byte[] generateKey(){
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[Constants.FILE.SYMMETRIC_SIZE];
        random.nextBytes(key);
        return key;
    }

    public static byte[] concatenateByteArray(byte[] a, byte[] b){
        byte[] concatenated = new byte[a.length + b.length];
        System.arraycopy(a, 0, concatenated, 0, a.length);
        System.arraycopy(b, 0, concatenated, a.length, b.length);
        return concatenated;
    }

    public static byte[] generateHash(byte[] file) {
        return DigestUtils.sha512(file);
    }

    public static byte[] sign(byte[] ks, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(Constants.Keys.CA_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(ks);
        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e) {
            logger.info("Cant sign with key");
            return null;
        }
    }

    public static byte[] unSign(byte[] ks, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(ks);
        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e) {
            logger.info("Cant unsign with key");
            return null;
        }
    }

    public static PublicKey getPublicKeyFrom(String usernameToGet) throws InvalidKeySpecException, NoSuchAlgorithmException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<Object>(new RequestPubKey(usernameToGet));
        ResponseEntity<byte[]> response = null;
        try {
            response = restTemplate.exchange(
                    Constants.CA.REQUEST_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<byte[]>(){});
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                logger.info("Not found");
                return null;
            }
        }
        return AuxMethods.decodePubKey(response.getBody());
    }

    private static byte[] cipher(byte[] plain, byte[] ivBytes, SecretKeySpec keySpec){
        try {
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            Cipher c = Cipher.getInstance(Constants.Keys.SYMMETRIC_ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            return c.doFinal(plain);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            logger.info("Cant cipher with key");
            throw new RuntimeException(e);
        }
    }

    private static byte[] decipher(byte[] plain, byte[] ivBytes, SecretKeySpec keySpec){
        try {
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            Cipher c = Cipher.getInstance(Constants.Keys.SYMMETRIC_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, keySpec, iv);
            return c.doFinal(plain);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            logger.info("Cant decipher with key");
            throw new RuntimeException(e);
        }
    }


    public static Path savePubKey(PublicKey publicKey, String username) throws IOException {
        Path path = Paths.get(Constants.Keys.KEYS_LOCATION + username + ".pub");
        Files.createDirectories(path.getParent());
        Files.write(path, publicKey.getEncoded());
        return path;
    }

    public static Path savePrivKey(PrivateKey privateKey, String username, String password) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] salt = generateSalt();

        SecretKeySpec keySpec = generateSecretKey(password, salt);

        byte[] ivBytes = generateIV();

        byte[] encValue = cipher(privateKey.getEncoded(), ivBytes, keySpec);

        byte[] finalCiphertext = new byte[encValue.length + Constants.FILE.IV_SIZE + Constants.FILE.SALT_SIZE];
        System.arraycopy(ivBytes, 0, finalCiphertext, 0, 16);
        System.arraycopy(salt, 0, finalCiphertext, 16, 16);
        System.arraycopy(encValue, 0, finalCiphertext, 32, encValue.length);

        Path path = Paths.get(Constants.Keys.KEYS_LOCATION + username + ".priv");
        Files.createDirectories(path.getParent());
        Files.write(path, finalCiphertext);

        return path;
    }

    public static PrivateKey loadPrivKey(Path path, String password) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException{
        byte[] file = Files.readAllBytes(path);
        byte[] ivBytes = new byte[Constants.FILE.IV_SIZE];
        byte[] salt = new byte[Constants.FILE.SALT_SIZE];
        int encodedSize = file.length - (Constants.FILE.IV_SIZE + Constants.FILE.SALT_SIZE);
        byte[] encodedFile = new byte[encodedSize];

        System.arraycopy(file, 0, ivBytes, 0, 16);
        System.arraycopy(file, 16, salt, 0, 16);
        System.arraycopy(file, (Constants.FILE.IV_SIZE + Constants.FILE.SALT_SIZE), encodedFile, 0, encodedSize);

        SecretKeySpec keySpec = generateSecretKey(password, salt);
        byte[] value = decipher(encodedFile, ivBytes, keySpec);

        return decodePrivKey(value);
    }

    public static PublicKey loadPubKey(Path path) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] file = Files.readAllBytes(path);
        return decodePubKey(file);
    }

    private static byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[Constants.FILE.SALT_SIZE];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] generateIV(){
        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[Constants.FILE.IV_SIZE];
        random.nextBytes(ivBytes);
        return ivBytes;
    }


    private static SecretKeySpec generateSecretKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key = f.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, Constants.FILE.SYMMETRIC_ALGORITHM);
    }

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

    public static void deleteFile(String pathFile){
        try {
            Path path = Paths.get(pathFile);
            Files.delete(path);
        } catch (IOException e) {
            logger.info("problem deleting file");
        }
    }

    public static String getFileName(UserFileResponse userFileResponse){
        return userFileResponse.getFileName() + "_" + userFileResponse.getFileOwner();
    }

    public static void logout(ClientShell shell){
        shell.setActiveSessionId(-1);
        shell.setActiveUser("");
        shell.setPathToDownload("/tmp/remote/");
        ClientShell.keyManager.setPrivateKey(null);
        ClientShell.keyManager.setPublicKey(null);
    }

    public static byte[] cipherWithKS(byte[] bytes, byte[] ks) {
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
    public static byte[] decipherWithKS(byte[] bytes, byte[] ks) {
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
            //e.printStackTrace();
            return null;
        }
    }

}
