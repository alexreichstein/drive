package se.drive.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.drive.dto.CreateFolderRequest;
import se.drive.model.Folder;
import se.drive.service.FolderService;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    public Folder createFolder(@RequestBody CreateFolderRequest request) {
        return folderService.createFolder(
                request.getName(),
                request.getParentId()
        );
    }
}