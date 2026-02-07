package se.drive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.drive.model.FileEntity;

import java.util.List;

/**
 * Repository för filentiteter.
 * Hanterar databasoperationer för filer.
 */
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    /**
     * Hämtar alla filer i en specifik mapp.
     *
     * @param folderId Mappens ID
     * @return Lista med filer i mappen
     */
    List<FileEntity> findByFolderId(Long folderId);
}