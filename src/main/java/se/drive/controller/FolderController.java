package se.drive.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import se.drive.dto.CreateFolderRequest;
import se.drive.dto.FolderModel;
import se.drive.model.Folder;
import se.drive.model.User;
import se.drive.service.FolderService;
import se.drive.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * REST API för mapphantering med HATEOAS.
 * Hanterar skapande av mappar och undermappar.
 * Returnerar hypermedia-länkar för navigering.
 * Kräver GitHub OAuth-autentisering.
 */
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;
    private final UserService userService;

    public FolderController(FolderService folderService, UserService userService) {
        this.folderService = folderService;
        this.userService = userService;
    }

    /**
     * Skapar en ny mapp för den inloggade användaren.
     *
     * @param request Innehåller mappnamn och parent ID
     * @param principal Den inloggade GitHub-användaren
     * @return FolderModel med HATEOAS-länkar
     */
    @PostMapping
    public FolderModel createFolder(
            @RequestBody CreateFolderRequest request,
            @AuthenticationPrincipal OAuth2User principal) {

        User user = userService.getOrCreateUser(principal);
        Folder folder = folderService.createFolder(request.getName(), request.getParentId(), user);
        return toModel(folder);
    }

    /**
     * Hämtar alla mappar för den inloggade användaren.
     *
     * @param principal Den inloggade GitHub-användaren
     * @return CollectionModel med alla mappar och länkar
     */
    @GetMapping
    public CollectionModel<FolderModel> getAllFolders(@AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getOrCreateUser(principal);

        List<FolderModel> folders = folderService.getAllFoldersByUser(user).stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(folders,
                linkTo(methodOn(FolderController.class).getAllFolders(principal)).withSelfRel());
    }

    /**
     * Hämtar en specifik mapp med ID.
     *
     * @param id Mapp-ID
     * @param principal Den inloggade GitHub-användaren
     * @return FolderModel med HATEOAS-länkar
     */
    @GetMapping("/{id}")
    public FolderModel getFolder(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {

        User user = userService.getOrCreateUser(principal);
        Folder folder = folderService.getFolderById(id, user);
        return toModel(folder);
    }

    /**
     * Tar bort en mapp om användaren äger den.
     *
     * @param id Mapp-ID
     * @param principal Den inloggade GitHub-användaren
     */
    @DeleteMapping("/{id}")
    public void deleteFolder(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {

        User user = userService.getOrCreateUser(principal);
        folderService.deleteFolder(id, user);
    }

    /**
     * Konverterar en Folder-entitet till FolderModel med HATEOAS-länkar.
     */
    private FolderModel toModel(Folder folder) {
        FolderModel model = new FolderModel(folder);

        // Lägg till self-link
        model.add(linkTo(methodOn(FolderController.class)
                .getFolder(folder.getId(), null)).withSelfRel());

        // Lägg till link till alla mappar
        model.add(linkTo(methodOn(FolderController.class)
                .getAllFolders(null)).withRel("all-folders"));

        // Lägg till link till föräldramapp om den finns
        if (folder.getParent() != null) {
            model.add(linkTo(methodOn(FolderController.class)
                    .getFolder(folder.getParent().getId(), null)).withRel("parent"));
        }

        // Lägg till delete-link
        model.add(linkTo(FolderController.class)
                .slash(folder.getId()).withRel("delete"));

        return model;
    }
}