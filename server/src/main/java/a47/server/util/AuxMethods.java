package a47.server.util;


import a47.server.model.request.RequestPubKey;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class AuxMethods {

    private static PublicKey decodePubKey(byte[] encodedPubKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(encodedPubKey);
        KeyFactory kf = KeyFactory.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
        return kf.generatePublic(ks);
    }

    public static PublicKey getPublicKeyFrom(String username, String usernameToGet) throws InvalidKeySpecException, NoSuchAlgorithmException { //TODO joao mete o http a retornar que o user nao existe na CA
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<Object>(new RequestPubKey(username, usernameToGet));
        ResponseEntity<byte[]> response = restTemplate.exchange(
                Constants.CA.REQUEST_URL,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<byte[]>(){});
        return AuxMethods.decodePubKey(response.getBody());
    }

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
