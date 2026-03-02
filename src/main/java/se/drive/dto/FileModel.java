package se.drive.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import se.drive.model.FileEntity;

/**
 * HATEOAS representation av FileEntity med hypermedia-länkar.
 */
@Getter
@Setter
public class FileModel extends RepresentationModel<FileModel> {

    private Long id;
    private String filename;
    private String contentType;
    private long size;
    private Long folderId;
    private String folderName;
    private String username;

    public FileModel(FileEntity file) {
        this.id = file.getId();
        this.filename = file.getFilename();
        this.contentType = file.getContentType();
        this.size = file.getSize();
        this.folderId = file.getFolder().getId();
        this.folderName = file.getFolder().getName();
        this.username = file.getUser().getUsername();
    }
}