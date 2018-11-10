package a47.ca.keyManager;

import a47.ca.Constants;

import javax.crypto.Cipher;
import java.security.Key;

public class AuxMethods {
    public static byte[] cipherWithKey(byte[] data, Key key) throws Exception {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);

        } catch (Exception e) {
            throw new Exception();
        }
    }
}
