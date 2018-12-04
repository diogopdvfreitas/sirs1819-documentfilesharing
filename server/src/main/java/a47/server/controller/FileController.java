package a47.server.controller;

import a47.server.exception.ErrorMessage;
import a47.server.exception.UserNotFoundException;
import a47.server.model.request.DownloadFileRequest;
import a47.server.model.request.ShareFileRequest;
import a47.server.model.request.UnShareFileRequest;
import a47.server.model.request.UploadFileRequest;
import a47.server.service.AuthenticationService;
import a47.server.service.FileManagerService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("files")
public class FileController {

    private FileManagerService fileManagerService;

    private AuthenticationService authenticationService;

    @Autowired
    public FileController(FileManagerService fileManagerService, AuthenticationService authenticationService) {
        this.fileManagerService = fileManagerService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> addFile(@RequestHeader("token") @NotNull @NotBlank long token, @Valid @RequestBody UploadFileRequest uploadFileRequest){
        authenticationService.validateUser(token);
        return ResponseEntity.ok(fileManagerService.uploadFile(authenticationService.getLoggedInUser(token), uploadFileRequest.getFile(), uploadFileRequest.getFileKey()));
    }

    @PostMapping("/download")
    public ResponseEntity<?> getFile(@RequestHeader("token") @NotNull @NotBlank long token, @Valid @RequestBody DownloadFileRequest downloadFileRequest){
        authenticationService.validateUser(token);
        return ResponseEntity.ok(fileManagerService.downloadFile(authenticationService.getLoggedInUser(token), downloadFileRequest.getFileId()));
    }

    @PostMapping("/share")
    public ResponseEntity<?> shareFile(@RequestHeader("token") @NotNull @NotBlank long token, @Valid @RequestBody ShareFileRequest shareFileRequest){
        authenticationService.validateUser(token);
        String username = authenticationService.getLoggedInUser(token);
        if(!authenticationService.userExists(shareFileRequest.getTargetUsername()))
            throw new UserNotFoundException(ErrorMessage.CODE_SERVER_USER_NOT_FOUND, "User '" + shareFileRequest.getTargetUsername() + "' not found");
        fileManagerService.shareFile(username, shareFileRequest.getTargetUsername(), shareFileRequest.getFileId(), shareFileRequest.getFileKey());
        return ResponseEntity.ok("File shared with success");
    }

    @PostMapping("/unshare")
    public ResponseEntity<?> unShareFile(@RequestHeader("token") @NotNull @NotBlank long token, @Valid @RequestBody UnShareFileRequest unShareFileRequest){
        authenticationService.validateUser(token);
        String username = authenticationService.getLoggedInUser(token);
        if(!authenticationService.userExists(unShareFileRequest.getTargetUsername()))
            throw new UserNotFoundException(ErrorMessage.CODE_SERVER_USER_NOT_FOUND, "User '" + unShareFileRequest.getTargetUsername() + "' not found");
        fileManagerService.unShareFile(username, unShareFileRequest.getTargetUsername(), unShareFileRequest.getFileId());
        return ResponseEntity.ok("File unshared with success");
    }


    @PostMapping("/convert")
    public ResponseEntity<?> convertFile(@RequestParam("file") MultipartFile file){
        String content = "";
        try {
             content = Base64.encodeBase64String(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(content);
    }
}
