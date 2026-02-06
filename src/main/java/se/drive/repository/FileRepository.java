package se.drive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.drive.model.FileEntity;

public interface FileRepository
        extends JpaRepository<FileEntity, Long> {
}