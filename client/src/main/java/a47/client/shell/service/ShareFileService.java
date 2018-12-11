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
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class ShareFileService {
    private static Logger logger = Logger.getLogger(ShareFileService.class);

    public Boolean unshareFile (long token, String userToUnShare, String fileId){
        Boolean sendtoServer = sendtoServer(token, new UnShareFileRequest(userToUnShare, fileId));
        if(sendtoServer == null)
            return false;
        return sendtoServer;
    }

    public Boolean shareFile(String userToShare, long token, String fileId){
        DownloadFileResponse file = getFileFromServer(token, fileId);//TODO sometimes this fail for no reason
        if(file == null )
            return false;
        //Decipher KS
        byte[] ks = AuxMethods.unSign(file.getFileKey(), ClientShell.keyManager.getPrivateKey());
        if (ks == null) {
            logger.error("Unsign KS");
            return false;
        }
        //Get publicKey from CA of user to share.
        PublicKey publicKeyUserToShare;
        try {
            publicKeyUserToShare = AuxMethods.getPublicKeyFrom(userToShare);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            logger.error("Get Public Key");
            return false;
        }
        byte[] ksSigned = AuxMethods.sign(ks, publicKeyUserToShare);
        if (ksSigned == null) {
            logger.error("Signing KS");
            return false;
        }
        Boolean sendtoServer = sendtoServer(token, new ShareFileRequest(userToShare, fileId, ksSigned));
        if(sendtoServer!=null)
            return sendtoServer;
        return false;
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
            }else if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                logger.info("Not found");
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
        } catch (HttpClientErrorException e) {//TODO NOT_FOUND do file ou user? fix this
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                ClientShell.setValidToken(false);
                return null;
            }
            else if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                logger.info("Not found");
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
        } catch (HttpClientErrorException e) {//TODO NOT_FOUND do file ou user? fix this
            if(e.getStatusCode() == HttpStatus.UNAUTHORIZED){
                ClientShell.setValidToken(false);
                return null;
            }
            else if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                logger.info("Not found");
                return null;
            }
        }
        return null;
    }

}
