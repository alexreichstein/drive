package se.drive.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.drive.exception.ResourceNotFoundException;
import se.drive.model.FileEntity;
import se.drive.model.Folder;
import se.drive.repository.FileRepository;
import se.drive.repository.FolderRepository;
import se.drive.storage.StorageService;

import java.io.IOException;

/**
 * Service för filhantering.
 * Hanterar affärslogik för uppladdning, nedladdning och borttagning av filer.
 */
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final StorageService storageService;

    public FileService(FileRepository fileRepository,
                       FolderRepository folderRepository,
                       StorageService storageService) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.storageService = storageService;
    }

    /**
     * Laddar upp en fil till en specifik mapp.
     *
     * @param file     Filen som ska laddas upp
     * @param folderId ID för målmappen
     * @return Den sparade filentiteten
     * @throws ResourceNotFoundException om mappen inte hittas
     * @throws IOException om filsparning misslyckas
     */
    public FileEntity uploadFile(MultipartFile file, Long folderId) throws IOException {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mapp med ID " + folderId + " hittades inte"
                ));

        String path = storageService.save(file);

        FileEntity entity = new FileEntity();
        entity.setFilename(file.getOriginalFilename());
        entity.setContentType(file.getContentType());
        entity.setSize(file.getSize());
        entity.setStoragePath(path);
        entity.setFolder(folder);

        return fileRepository.save(entity);
    }

    /**
     * Laddar ner en fil från disk.
     *
     * @param fileId Filens ID
     * @return Filens innehåll som byte array
     * @throws ResourceNotFoundException om filen inte hittas
     * @throws IOException om filladdning misslyckas
     */
    public byte[] downloadFile(Long fileId) throws IOException {
        FileEntity file = findFileById(fileId);
        return storageService.load(file.getStoragePath());
    }

    /**
     * Tar bort en fil permanent (både från disk och databas).
     *
     * @param fileId Filens ID
     * @throws ResourceNotFoundException om filen inte hittas
     * @throws IOException om filborttagning misslyckas
     */
    public void deleteFile(Long fileId) throws IOException {
        FileEntity file = findFileById(fileId);

        storageService.delete(file.getStoragePath());
        fileRepository.delete(file);
    }

    /**
     * Hämtar filnamnet för en fil.
     *
     * @param fileId Filens ID
     * @return Filnamnet
     * @throws ResourceNotFoundException om filen inte hittas
     */
    public String getFilename(Long fileId) {
        FileEntity file = findFileById(fileId);
        return file.getFilename();
    }

    /**
     * Hjälpmetod för att hitta en fil eller kasta exception.
     *
     * @param fileId Filens ID
     * @return Filentiteten
     * @throws ResourceNotFoundException om filen inte hittas
     */
    private FileEntity findFileById(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fil med ID " + fileId + " hittades inte"
                ));
    }
}