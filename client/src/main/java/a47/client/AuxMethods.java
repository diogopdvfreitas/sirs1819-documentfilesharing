package a47.client;


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
            cipher = Cipher.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
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

    public static byte[] decipherWithPrivateKey(byte[] cipheredData, PrivateKey privateKey) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
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
}
