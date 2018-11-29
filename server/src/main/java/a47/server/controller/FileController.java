package a47.server.controller;

import a47.server.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("files")
public class FileController {

    private FileStorageService fileStorageService;

    @Autowired
    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<?> addFile(@RequestParam("username") @NotNull @NotBlank String username, @RequestParam("file") @NotNull MultipartFile file){ //TODO Maybe create model with MultipartFile in it
        fileStorageService.uploadFile(username, file);
        return ResponseEntity.ok(file.getSize());
    }
}
