package se.drive.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Service för fysisk fillagring på disk.
 * Hanterar sparande, laddning och borttagning av filer i filsystemet.
 */
@Service
public class StorageService {

    @Value("${storage.root}")
    private String rootDir;

    /**
     * Sparar en uppladdad fil till disk.
     * Genererar ett unikt filnamn med UUID för att undvika namnkollisioner.
     *
     * @param file Filen som ska sparas
     * @return Sökvägen till den sparade filen
     * @throws IOException om filsparning misslyckas
     */
    public String save(MultipartFile file) throws IOException {
        // Skapa lagringskatalog om den inte finns
        Files.createDirectories(Path.of(rootDir));

        // Generera unikt filnamn: UUID_originalnamn
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = Path.of(rootDir, filename);

        // Spara filen
        file.transferTo(target);

        return target.toString();
    }

    /**
     * Laddar en fil från disk.
     *
     * @param path Sökvägen till filen
     * @return Filens innehåll som byte array
     * @throws IOException om filladdning misslyckas
     */
    public byte[] load(String path) throws IOException {
        return Files.readAllBytes(Path.of(path));
    }

    /**
     * Tar bort en fil från disk.
     *
     * @param path Sökvägen till filen
     * @throws IOException om filborttagning misslyckas
     */
    public void delete(String path) throws IOException {
        Files.deleteIfExists(Path.of(path));
    }
}