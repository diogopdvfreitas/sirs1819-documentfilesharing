package a47.server.security;

import a47.server.model.PasswordHash;

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

    public static PasswordHash createHashedPassword(String password){
        final int iterations = 1000;
        try {
            byte[] salt = generateSalt();
            byte[] hashedPassword = hashPassword(password, salt, iterations);
            return new PasswordHash(iterations, salt, hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validatePassword(String password, PasswordHash passwordHash){
        byte[] hashNewPassword = hashPassword(password, passwordHash.getSalt(), passwordHash.getIterations());
        return Arrays.equals(hashNewPassword, passwordHash.getPasswordHash());
    }


    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }
}
