package se.drive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.drive.exception.ResourceNotFoundException;
import se.drive.model.Folder;
import se.drive.model.User;
import se.drive.repository.FolderRepository;

import java.util.List;

/**
 * Service för mapphantering.
 * Hanterar affärslogik för skapande och hantering av mappar.
 * Alla operationer är kopplade till en specifik användare.
 */
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    /**
     * Skapar en ny mapp för en användare.
     *
     * @param name     Mappens namn
     * @param parentId ID för föräldramappen (null för rotmapp)
     * @param user     Användaren som äger mappen
     * @return Den skapade mappen
     * @throws ResourceNotFoundException om föräldramappen inte hittas
     * @throws SecurityException om användaren inte har behörighet till föräldramappen
     */
    public Folder createFolder(String name, Long parentId, User user) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setUser(user);

        if (parentId != null) {
            Folder parent = folderRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Föräldramapp med ID " + parentId + " hittades inte"
                    ));

            // Kontrollera att användaren äger föräldramappen
            if (!parent.getUser().getId().equals(user.getId())) {
                throw new SecurityException("Du har inte behörighet att skapa undermappar här");
            }

            folder.setParent(parent);
        }

        return folderRepository.save(folder);
    }

    /**
     * Hämtar alla mappar för en specifik användare.
     *
     * @param user Användaren vars mappar ska hämtas
     * @return Lista med användarens mappar
     */
    public List<Folder> getAllFoldersByUser(User user) {
        return folderRepository.findByUserId(user.getId());
    }

    /**
     * Hämtar en specifik mapp om användaren äger den.
     *
     * @param folderId Mapp-ID
     * @param user Användaren som försöker hämta mappen
     * @return Mappen
     * @throws ResourceNotFoundException om mappen inte hittas
     * @throws SecurityException om användaren inte äger mappen
     */
    public Folder getFolderById(Long folderId, User user) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mapp med ID " + folderId + " hittades inte"
                ));

        // Kontrollera att användaren äger mappen
        if (!folder.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Du har inte behörighet att se denna mapp");
        }

        return folder;
    }


    /**
     * Tar bort en mapp om användaren äger den.
     *
     * @param folderId Mapp-ID
     * @param user Användaren som försöker ta bort mappen
     * @throws ResourceNotFoundException om mappen inte hittas
     * @throws SecurityException om användaren inte äger mappen
     */
    public void deleteFolder(Long folderId, User user) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mapp med ID " + folderId + " hittades inte"
                ));

        // Kontrollera att användaren äger mappen
        if (!folder.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Du har inte behörighet att ta bort denna mapp");
        }

        folderRepository.delete(folder);
    }
}