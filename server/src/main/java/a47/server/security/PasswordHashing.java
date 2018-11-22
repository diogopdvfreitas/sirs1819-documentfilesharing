package a47.server.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class PasswordHashing {


    private static byte[] hashPassword(String password, byte[] salt, int iterations){
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, 512);
            SecretKey key = secretKeyFactory.generateSecret(pbeKeySpec);
            return key.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHashedPassword(String password){
        final int iterations = 1000;
        try {
            byte[] salt = generateSalt();
            byte[] hashedPassword = hashPassword(password, salt, iterations);
            return String.valueOf(iterations) + ":" + Base64.encodeBase64String(hashedPassword)  + ":" + Base64.encodeBase64String(salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validatePassword(String password, String storedPassword){
        String[] passwordParts = storedPassword.split(":");
        final int iterations = Integer.parseInt(passwordParts[0]);
        final byte[] storedHashedPassword = Base64.decodeBase64(passwordParts[1]);
        final byte[] salt = Base64.decodeBase64(passwordParts[2]);

        byte[] hashedPassword = hashPassword(password, salt, iterations);

        return Arrays.equals(hashedPassword, storedHashedPassword);
    }


    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }
}
