package se.drive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.drive.model.Folder;

public interface FolderRepository
        extends JpaRepository<Folder, Long> {
}
