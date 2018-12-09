package a47.client.shell.service;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.model.DownloadFile;
import a47.client.shell.model.ShareFileRequest;
import a47.client.shell.model.UnShareFileRequest;
import a47.client.shell.model.response.DownloadFileResponse;
import org.jboss.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class ShareFileService {
    private static Logger logger = Logger.getLogger(ShareFileService.class);

    public Boolean unshareFile (long token, String userToUnShare, String fileId){
        return sendtoServer(token, new UnShareFileRequest(userToUnShare, fileId));
    }

    public Boolean shareFile(String username, String userToShare, long token, String fileId){
        DownloadFileResponse file = getFileFromServer(token, fileId);
        //Decipher KS
        byte[] ks = AuxMethods.unSign(file.getFileKey(), ClientShell.keyManager.getPrivateKey());
        if (ks == null) {
            logger.error("Unsign KS");
            return null;
        }
        //Get publicKey from CA of user to share.
        PublicKey publicKeyUserToShare = null;
        try {
            publicKeyUserToShare = AuxMethods.getPublicKeyFrom(username, userToShare);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            logger.error("Get Public Key");
            return null;
        }

        byte[] ksSigned = AuxMethods.sign(ks, publicKeyUserToShare);
        if (ksSigned == null) {
            logger.error("Signing KS");
            return null;
        }
        return sendtoServer(token, new ShareFileRequest(userToShare, fileId, ksSigned));
    }

    private DownloadFileResponse getFileFromServer(long token, String fileId){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        DownloadFile downloadFile = new DownloadFile(fileId);
        HttpEntity<?> httpEntity = new HttpEntity<Object>(downloadFile, headers);
        try {
            return restTemplate.postForObject(Constants.FILE.DOWNLOAD_FILE_SERVER_URL, httpEntity, DownloadFileResponse.class);
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                ClientShell.setValidToken(false);
                return null;
            }
        }
        return null;
    }

    private Boolean sendtoServer(long token, ShareFileRequest shareFileRequest){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        HttpEntity<?> httpEntity = new HttpEntity<Object>(shareFileRequest, headers);
        try {
            return restTemplate.postForObject(Constants.FILE.SHARE_FILE_SERVER_URL, httpEntity, Boolean.class);
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                ClientShell.setValidToken(false);
                return null;
            }
        }
        return null;
    }

    private Boolean sendtoServer(long token, UnShareFileRequest unShareFileRequest){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", String.valueOf(token));
        HttpEntity<?> httpEntity = new HttpEntity<Object>(unShareFileRequest, headers);
        try {
            return restTemplate.postForObject(Constants.FILE.UNSHARE_FILE_SERVER_URL, httpEntity, Boolean.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

}
