package se.drive.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.drive.model.FileEntity;
import se.drive.service.FileService;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    //  Upload file
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public FileEntity upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("folderId") Long folderId
    ) throws Exception {

        //  Tillfällig debug (ta bort sen)
        System.out.println("---- UPLOAD DEBUG ----");
        System.out.println("Original filename: " + file.getOriginalFilename());
        System.out.println("Size: " + file.getSize());
        System.out.println("Content type: " + file.getContentType());
        System.out.println("Is empty: " + file.isEmpty());
        System.out.println("Bytes length: " + file.getBytes().length);
        System.out.println("----------------------");

        return fileService.uploadFile(file, folderId);
    }

    //  Download file
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {

        byte[] data = fileService.downloadFile(id);
        String filename = fileService.getFilename(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    //  Delete file
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}