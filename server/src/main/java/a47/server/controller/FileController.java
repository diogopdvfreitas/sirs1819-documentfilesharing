package a47.server.controller;

import a47.server.exception.ErrorMessage;
import a47.server.exception.UserNotFoundException;
import a47.server.model.File;
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
    public ResponseEntity<?> addFile(@RequestHeader("token") @NotNull @NotBlank long token, @Valid @RequestBody File file){
        authenticationService.validateUser(token);

        return ResponseEntity.ok(fileManagerService.uploadFile(authenticationService.getLoggedInUser(token), file));
    }

    @PostMapping("/download")
    public ResponseEntity<?> getFile(@RequestHeader("token") @NotNull @NotBlank long token, @RequestParam("fileId") String fileId){
        authenticationService.validateUser(token);
        return ResponseEntity.ok(fileManagerService.downloadFile(authenticationService.getLoggedInUser(token), fileId));
    }

    @PostMapping("/share")
    public ResponseEntity<?> shareFile(@RequestHeader("token") @NotNull @NotBlank long token, @RequestParam("username") String targetUsername, @RequestParam("fileId") String fileId){
        authenticationService.validateUser(token);
        String username = authenticationService.getLoggedInUser(token);
        if(!authenticationService.userExists(targetUsername))
            throw new UserNotFoundException(ErrorMessage.CODE_SERVER_USER_NOT_FOUND, "User '" + targetUsername + "' not found");
        fileManagerService.shareFile(username, targetUsername, fileId);
        return ResponseEntity.ok("File shared with success");
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
