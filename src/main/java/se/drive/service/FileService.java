package se.drive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.drive.exception.ResourceNotFoundException;
import se.drive.model.FileEntity;
import se.drive.model.Folder;
import se.drive.model.User;
import se.drive.repository.FileRepository;
import se.drive.repository.FolderRepository;
import se.drive.storage.StorageService;

import java.io.IOException;
import java.util.List;

/**
 * Service för filhantering.
 * Hanterar affärslogik för uppladdning, nedladdning och borttagning av filer.
 * Alla operationer är kopplade till en specifik användare.
 */
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final StorageService storageService;

    /**
     * Laddar upp en fil till en specifik mapp.
     *
     * @param file     Filen som ska laddas upp
     * @param folderId ID för målmappen
     * @param user     Användaren som laddar upp filen
     * @return Den sparade filentiteten
     * @throws ResourceNotFoundException om mappen inte hittas
     * @throws SecurityException om användaren inte äger mappen
     * @throws IOException om filsparning misslyckas
     */
    public FileEntity uploadFile(MultipartFile file, Long folderId, User user) throws IOException {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mapp med ID " + folderId + " hittades inte"
                ));

        // Kontrollera att användaren äger mappen
        if (!folder.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Du har inte behörighet att ladda upp filer till denna mapp");
        }

        String path = storageService.save(file);

        FileEntity entity = new FileEntity();
        entity.setFilename(file.getOriginalFilename());
        entity.setContentType(file.getContentType());
        entity.setSize(file.getSize());
        entity.setStoragePath(path);
        entity.setFolder(folder);
        entity.setUser(user);

        return fileRepository.save(entity);
    }

    /**
     * Hämtar alla filer för en specifik användare.
     *
     * @param user Användaren vars filer ska hämtas
     * @return Lista med användarens filer
     */
    public List<FileEntity> getAllFilesByUser(User user) {
        return fileRepository.findByUserId(user.getId());
    }

    /**
     * Hämtar en specifik fil om användaren äger den.
     *
     * @param fileId Filens ID
     * @param user   Användaren som försöker hämta filen
     * @return Filentiteten
     * @throws ResourceNotFoundException om filen inte hittas
     * @throws SecurityException om användaren inte äger filen
     */
    public FileEntity getFileById(Long fileId, User user) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fil med ID " + fileId + " hittades inte"
                ));

        // Kontrollera att användaren äger filen
        if (!file.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Du har inte behörighet att se denna fil");
        }

        return file;
    }

    /**
     * Laddar ner en fil från disk.
     *
     * @param fileId Filens ID
     * @param user   Användaren som försöker ladda ner filen
     * @return Filens innehåll som byte array
     * @throws ResourceNotFoundException om filen inte hittas
     * @throws SecurityException om användaren inte äger filen
     * @throws IOException om filladdning misslyckas
     */
    public byte[] downloadFile(Long fileId, User user) throws IOException {
        FileEntity file = getFileById(fileId, user);
        return storageService.load(file.getStoragePath());
    }

    /**
     * Tar bort en fil permanent (både från disk och databas).
     *
     * @param fileId Filens ID
     * @param user   Användaren som försöker ta bort filen
     * @throws ResourceNotFoundException om filen inte hittas
     * @throws SecurityException om användaren inte äger filen
     * @throws IOException om filborttagning misslyckas
     */
    public void deleteFile(Long fileId, User user) throws IOException {
        FileEntity file = getFileById(fileId, user);

        storageService.delete(file.getStoragePath());
        fileRepository.delete(file);
    }

    /**
     * Hämtar filnamnet för en fil.
     *
     * @param fileId Filens ID
     * @param user   Användaren som försöker hämta filnamnet
     * @return Filnamnet
     * @throws ResourceNotFoundException om filen inte hittas
     * @throws SecurityException om användaren inte äger filen
     */
    public String getFilename(Long fileId, User user) {
        FileEntity file = getFileById(fileId, user);
        return file.getFilename();
    }
}