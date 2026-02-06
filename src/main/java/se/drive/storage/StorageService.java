package se.drive.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${storage.root}")
    private String rootDir;

    public String save(MultipartFile file) throws IOException {
        Files.createDirectories(Path.of(rootDir));

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = Path.of(rootDir, filename);

        file.transferTo(target);

        return target.toString();
    }

    public byte[] load(String path) throws IOException {
        return Files.readAllBytes(Path.of(path));
    }

    public void delete(String path) throws IOException {
        Files.deleteIfExists(Path.of(path));
    }
}