package se.drive.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.drive.dto.FileModel;
import se.drive.model.FileEntity;
import se.drive.model.User;
import se.drive.service.FileService;
import se.drive.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * REST API för filhantering med HATEOAS.
 * Hanterar uppladdning, nedladdning och borttagning av filer.
 * Returnerar hypermedia-länkar för navigering.
 * Kräver GitHub OAuth-autentisering.
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    /**
     * Laddar upp en fil till en specifik mapp.
     *
     * @param file      Filen som ska laddas upp
     * @param folderId  ID för mappen där filen ska sparas
     * @param principal Den inloggade GitHub-användaren
     * @return FileModel med HATEOAS-länkar
     * @throws Exception om uppladdningen misslyckas
     */
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public FileModel upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("folderId") Long folderId,
            @AuthenticationPrincipal OAuth2User principal
    ) throws Exception {
        User user = userService.getOrCreateUser(principal);
        FileEntity fileEntity = fileService.uploadFile(file, folderId, user);
        return toModel(fileEntity);
    }

    /**
     * Hämtar alla filer för den inloggade användaren.
     *
     * @param principal Den inloggade GitHub-användaren
     * @return CollectionModel med alla filer och länkar
     */
    @GetMapping
    public CollectionModel<FileModel> getAllFiles(@AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getOrCreateUser(principal);

        List<FileModel> files = fileService.getAllFilesByUser(user).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(files,
                linkTo(methodOn(FileController.class).getAllFiles(principal)).withSelfRel());
    }

    /**
     * Hämtar metadata för en specifik fil.
     *
     * @param id        Filens ID
     * @param principal Den inloggade GitHub-användaren
     * @return FileModel med HATEOAS-länkar
     */
    @GetMapping("/{id}/metadata")
    public FileModel getFileMetadata(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {

        User user = userService.getOrCreateUser(principal);
        FileEntity file = fileService.getFileById(id, user);
        return toModel(file);
    }

    /**
     * Laddar ner en fil.
     *
     * @param id        Filens ID
     * @param principal Den inloggade GitHub-användaren
     * @return Filen som byte array med rätt headers för nedladdning
     * @throws Exception om filen inte hittas
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) throws Exception {

        User user = userService.getOrCreateUser(principal);
        byte[] data = fileService.downloadFile(id, user);
        String filename = fileService.getFilename(id, user);

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
     * @param id        Filens ID
     * @param principal Den inloggade GitHub-användaren
     * @return 204 No Content vid lyckad borttagning
     * @throws Exception om filen inte hittas
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) throws Exception {

        User user = userService.getOrCreateUser(principal);
        fileService.deleteFile(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Konverterar en FileEntity till FileModel med HATEOAS-länkar.
     */
    private FileModel toModel(FileEntity file) {
        FileModel model = new FileModel(file);

        // Lägg till self-link (metadata)
        model.add(linkTo(methodOn(FileController.class)
                .getFileMetadata(file.getId(), null)).withSelfRel());

        // Lägg till download-link
        model.add(linkTo(FileController.class)
                .slash(file.getId()).slash("download").withRel("download"));

        // Lägg till link till alla filer
        model.add(linkTo(methodOn(FileController.class)
                .getAllFiles(null)).withRel("all-files"));

        // Lägg till link till mappen filen tillhör
        model.add(linkTo(FolderController.class)
                .slash(file.getFolder().getId()).withRel("folder"));

        // Lägg till delete-link
        model.add(linkTo(FileController.class)
                .slash(file.getId()).withRel("delete"));

        return model;
    }
}