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

    // 📤 Upload file
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public FileEntity upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folderId") Long folderId
    ) throws Exception {
        return fileService.uploadFile(file, folderId);
    }

    // 📥 Download file
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {

        byte[] data = fileService.downloadFile(id);
        String filename = fileService.getFilename(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    // 🗑 Delete file
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}