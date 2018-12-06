package a47.client;


import a47.client.shell.ClientShell;
import a47.client.shell.model.Challenge;
import a47.client.shell.model.RequestPubKey;
import a47.client.shell.model.response.ChallengeResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class AuxMethods {
    public static byte[] cipherWithKey(byte[] data, Key key) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(Constants.Keys.CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decipherWithPrivateKey(byte[] cipheredData, Key privateKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(Constants.Keys.CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipheredData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PublicKey decodePubKey(byte[] encodedPubKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(encodedPubKey);
        KeyFactory kf = KeyFactory.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
        return kf.generatePublic(ks);
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
            Cipher cipher = Cipher.getInstance(Constants.Keys.CIPHER);
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

    public static PublicKey getPublicKeyFrom(String username, String usernameToGet) throws InvalidKeySpecException, NoSuchAlgorithmException {
        RestTemplate restTemplate = new RestTemplate();
        Challenge challenge = restTemplate.postForObject(Constants.CA.REQUEST_URL, new RequestPubKey(username, usernameToGet), Challenge.class);
        byte[] unCipheredChallenge = AuxMethods.decipherWithPrivateKey(challenge.getChallenge(), ClientShell.keyManager.getPrivateKey());
        HttpEntity<?> entity = new HttpEntity<Object>(new ChallengeResponse(challenge.getUUID(), challenge.getUsername(), unCipheredChallenge));
        ResponseEntity<byte[]> response = restTemplate.exchange(
                Constants.CA.REQUEST_RESPONSE_URL,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<byte[]>(){});
        return AuxMethods.decodePubKey(response.getBody());
    }
}
