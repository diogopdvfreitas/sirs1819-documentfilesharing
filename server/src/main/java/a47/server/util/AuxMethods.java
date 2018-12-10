package a47.server.util;


import a47.server.exception.ErrorMessage;
import a47.server.exception.PublicKeyNotFoundException;
import a47.server.exception.ServerException;
import a47.server.model.request.RequestPubKey;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class AuxMethods {

    private static PublicKey decodePubKey(byte[] encodedPubKey) {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(encodedPubKey);
        try {
            KeyFactory kf = KeyFactory.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
            return kf.generatePublic(ks);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ServerException("Error decoding public key");
        }
    }

    public static PublicKey getPublicKeyFrom(String username, String usernameToGet) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<Object>(new RequestPubKey(username, usernameToGet));
        ResponseEntity<byte[]> response;
        try {
            response = restTemplate.exchange(
                    Constants.CA.REQUEST_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<byte[]>(){});
            return AuxMethods.decodePubKey(response.getBody());
        } catch (HttpClientErrorException e) {
           if(e.getStatusCode() == HttpStatus.NOT_FOUND)
               throw new PublicKeyNotFoundException(ErrorMessage.CODE_SERVER_GENERAL, "Public key not found in CA");
        }
        return null;
    }

    public static byte[] cipherWithKey(byte[] data, Key key) throws Exception {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);

        } catch (Exception e) {
            throw new Exception();
        }
    }
}
