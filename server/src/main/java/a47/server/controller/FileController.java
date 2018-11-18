package a47.server.controller;

import a47.server.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("files")
public class FileController {

    private FileStorageService fileStorageService;

    @Autowired
    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<?> addFile(@PathParam("file") MultipartFile file){ //TODO Maybe create model with MultipartFile in it
        fileStorageService.uploadFile(file);
        return ResponseEntity.ok(file.getSize());
    }
}
