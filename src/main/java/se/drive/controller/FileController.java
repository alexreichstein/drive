package se.drive.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.drive.model.FileEntity;
import se.drive.service.FileService;

/**
 * REST API för filhantering.
 * Hanterar uppladdning, nedladdning och borttagning av filer.
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Laddar upp en fil till en specifik mapp.
     *
     * @param file     Filen som ska laddas upp
     * @param folderId ID för mappen där filen ska sparas
     * @return Den sparade filentiteten med metadata
     * @throws Exception om uppladdningen misslyckas
     */
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public FileEntity upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("folderId") Long folderId
    ) throws Exception {
        return fileService.uploadFile(file, folderId);
    }

    /**
     * Laddar ner en fil.
     *
     * @param id Filens ID
     * @return Filen som byte array med rätt headers för nedladdning
     * @throws Exception om filen inte hittas
     */
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

    /**
     * Tar bort en fil permanent.
     *
     * @param id Filens ID
     * @return 204 No Content vid lyckad borttagning
     * @throws Exception om filen inte hittas
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}