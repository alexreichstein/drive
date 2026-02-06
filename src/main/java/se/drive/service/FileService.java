package se.drive.service;
import se.drive.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.drive.exception.ResourceNotFoundException;
import se.drive.model.FileEntity;
import se.drive.model.Folder;
import se.drive.repository.FileRepository;
import se.drive.repository.FolderRepository;

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

    // 📤 Upload file
    public FileEntity uploadFile(MultipartFile file, Long folderId) throws Exception {

        // 🔍 TEMP DEBUG – LÄGG HÄR
        System.out.println("---- UPLOAD DEBUG ----");
        System.out.println("Original filename: " + file.getOriginalFilename());
        System.out.println("Size: " + file.getSize());
        System.out.println("Content type: " + file.getContentType());
        System.out.println("Is empty: " + file.isEmpty());
        System.out.println("----------------------");

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Folder not found"));

        String path = storageService.save(file);

        FileEntity entity = new FileEntity();
        entity.setFilename(file.getOriginalFilename());
        entity.setContentType(file.getContentType());
        entity.setSize(file.getSize());
        entity.setStoragePath(path);
        entity.setFolder(folder);

        return fileRepository.save(entity);
    }

    // 📥 Download file
    public byte[] downloadFile(Long fileId) throws Exception {

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found"));

        return storageService.load(file.getStoragePath());
    }

    // ❌ Delete file
    public void deleteFile(Long fileId) throws Exception {

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found"));

        storageService.delete(file.getStoragePath());
        fileRepository.delete(file);


    }
    public String getFilename(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found")
                );

        return file.getFilename();
    }
}