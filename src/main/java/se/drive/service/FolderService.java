package se.drive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.drive.exception.ResourceNotFoundException;
import se.drive.model.Folder;
import se.drive.repository.FolderRepository;

/**
 * Service för mapphantering.
 * Hanterar affärslogik för skapande och hantering av mappar.
 */
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    /**
     * Skapar en ny mapp.
     *
     * @param name     Mappens namn
     * @param parentId ID för föräldramappen (null för rotmapp)
     * @return Den skapade mappen
     * @throws ResourceNotFoundException om föräldramappen inte hittas
     */
    public Folder createFolder(String name, Long parentId) {
        Folder folder = new Folder();
        folder.setName(name);

        if (parentId != null) {
            Folder parent = folderRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Föräldramapp med ID " + parentId + " hittades inte"
                    ));
            folder.setParent(parent);
        }

        return folderRepository.save(folder);
    }
}