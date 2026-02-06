package se.drive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.drive.exception.ResourceNotFoundException;
import se.drive.model.Folder;
import se.drive.repository.FolderRepository;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    public Folder createFolder(String name, Long parentId) {

        Folder folder = new Folder();
        folder.setName(name);

        if (parentId != null) {
            Folder parent = folderRepository
                    .findById(parentId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Parent folder not found")
                    );

            folder.setParent(parent);
        }

        return folderRepository.save(folder);
    }
}