package se.drive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.drive.model.Folder;

import java.util.List;

/**
 * Repository för mappentiteter.
 * Hanterar databasoperationer för mappar.
 */
public interface FolderRepository extends JpaRepository<Folder, Long> {

    /**
     * Hämtar alla rotmappar (mappar utan förälder).
     *
     * @return Lista med rotmappar
     */
    List<Folder> findByParentIsNull();

    /**
     * Hämtar alla undermappar till en specifik mapp.
     *
     * @param parentId Föräldermappens ID
     * @return Lista med undermappar
     */
    List<Folder> findByParentId(Long parentId);
}