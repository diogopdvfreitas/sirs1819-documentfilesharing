package a47.client;


import a47.client.shell.ClientShell;
import a47.client.shell.model.RequestPubKey;
import a47.client.shell.model.response.UserFileResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    public static byte[] cipherWithKey(byte[] data, Key key) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(Constants.Keys.CA_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);

        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decipherWithPrivateKey(byte[] cipheredData, Key privateKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(Constants.Keys.CA_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipheredData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
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
            //e.printStackTrace();
            return null;
        }
    }

    public static byte[] unSign(byte[] ks, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(ks);
        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PublicKey getPublicKeyFrom(String usernameToGet) throws InvalidKeySpecException, NoSuchAlgorithmException {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<Object>(new RequestPubKey(usernameToGet));
        ResponseEntity<byte[]> response = restTemplate.exchange(
                Constants.CA.REQUEST_URL,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<byte[]>(){});
        return AuxMethods.decodePubKey(response.getBody());
    }

    public static byte[] cipher(byte[] plain, byte[] key){
        try {
            SecretKeySpec keyspec = new SecretKeySpec(key, Constants.FILE.SYMMETRIC_ALGORITHM);
            Cipher cipher = Cipher.getInstance(Constants.FILE.SYMMETRIC_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keyspec);
            return cipher.doFinal(plain);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static byte[] cipher(byte[] plain, byte[] ivBytes, SecretKeySpec keySpec){
        try {
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            Cipher c = Cipher.getInstance(Constants.Keys.SYMMETRIC_ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            return c.doFinal(plain);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static Path savePubKey(PublicKey publicKey, String username) throws IOException {
        Path path = Paths.get(Constants.Keys.KEYS_LOCATION + username + ".pub");
        Files.createDirectories(path.getParent());
        Files.write(path, publicKey.getEncoded());
        return path;
    }

    public static Path savePrivKey(PrivateKey privateKey, String username, String password) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
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

    public static PrivateKey loadPrivKey(Path path, String username, String password) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException{
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

    public static PublicKey loadPubKey(Path path, String username) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
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

    public static String getFileName(UserFileResponse userFileResponse){
        return userFileResponse.getFileName() + "_" + userFileResponse.getFileOwner();
    }

    public static void logout(ClientShell shell){
        shell.setActiveSessionId(-1);
        shell.setActiveUser("");
        ClientShell.keyManager.setPrivateKey(null);
        ClientShell.keyManager.setPublicKey(null);
    }
}
