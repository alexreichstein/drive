package se.drive.controller;

import org.springframework.web.bind.annotation.*;
import se.drive.dto.CreateFolderRequest;
import se.drive.model.Folder;
import se.drive.service.FolderService;

/**
 * REST API för mapphantering.
 * Hanterar skapande av mappar och undermappar.
 */
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    /**
     * Skapar en ny mapp.
     *
     * @param request Innehåller mappnamn och parent ID
     * @return Den skapade mappen
     */
    @PostMapping
    public Folder createFolder(@RequestBody CreateFolderRequest request) {
        return folderService.createFolder(
                request.getName(),
                request.getParentId()
        );
    }
}