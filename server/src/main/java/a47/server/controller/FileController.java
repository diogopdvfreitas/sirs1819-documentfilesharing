package a47.server.controller;

import a47.server.exception.ErrorMessage;
import a47.server.exception.UserNotFoundException;
import a47.server.model.File;
import a47.server.model.FileMetaData;
import a47.server.model.request.*;
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

    @PostMapping("/update")
    public ResponseEntity<?> updateFile(@RequestHeader("token") @NotNull @NotBlank long token, @Valid @RequestBody UpdateFileRequest updateFileRequest){
        authenticationService.validateUser(token);
        String username = authenticationService.getLoggedInUser(token);
        return ResponseEntity.ok(fileManagerService.updateFile(username, updateFileRequest));
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
            throw new UserNotFoundException(ErrorMessage.CODE_SERVER_USER_NOT_FOUND, "User '" + shareFileRequest.getTargetUsername() + "' not found"); //TODO aplicar isto no lado servidor
        fileManagerService.shareFile(username, shareFileRequest.getTargetUsername(), shareFileRequest.getFileId(), shareFileRequest.getFileKey());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/unshare")
    public ResponseEntity<?> unShareFile(@RequestHeader("token") @NotNull @NotBlank long token, @Valid @RequestBody UnShareFileRequest unShareFileRequest){
        authenticationService.validateUser(token);
        String username = authenticationService.getLoggedInUser(token);
        if(!authenticationService.userExists(unShareFileRequest.getTargetUsername()))
            throw new UserNotFoundException(ErrorMessage.CODE_SERVER_USER_NOT_FOUND, "User '" + unShareFileRequest.getTargetUsername() + "' not found");
        fileManagerService.unShareFile(username, unShareFileRequest.getTargetUsername(), unShareFileRequest.getFileId());
        return ResponseEntity.ok(true);
    }

    @PostMapping("/fileInfo")
    public ResponseEntity<?> fileInfo(@RequestHeader("token") @NotNull @NotBlank long token, @RequestBody String fileId){
        authenticationService.validateUser(token);
        String username = authenticationService.getLoggedInUser(token);
        return ResponseEntity.ok(fileManagerService.getFileMetadata(username, fileId));
    }

    @GetMapping("/listUserFiles")
    public ResponseEntity<?> listUserFiles(@RequestHeader("token") @NotNull @NotBlank long token){
        authenticationService.validateUser(token);
        String username = authenticationService.getLoggedInUser(token);
        return ResponseEntity.ok(fileManagerService.listUserFiles(username));
    }

    @PostMapping("/listAccessFileUser")
    public ResponseEntity<?> listAccessUserFiles(@RequestHeader("token") @NotNull @NotBlank long token, @Valid @RequestBody ListAccessUserFilesRequest listAccessUserFilesRequest){
        authenticationService.validateUser(token);
        String username = authenticationService.getLoggedInUser(token);
        return ResponseEntity.ok(fileManagerService.listAccessUserFiles(username, listAccessUserFilesRequest.getFileId()));
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkFile(@RequestHeader("token") @NotNull @NotBlank long token, @Valid @RequestBody FileMetaData fileMetaData){
        authenticationService.validateUser(token);
        String username = authenticationService.getLoggedInUser(token);
        return ResponseEntity.ok(fileManagerService.checkLastVersion(username, fileMetaData));
    }
}
