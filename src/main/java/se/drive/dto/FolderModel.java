package se.drive.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import se.drive.model.Folder;

/**
 * HATEOAS representation av Folder med hypermedia-länkar.
 */
@Getter
@Setter
public class FolderModel extends RepresentationModel<FolderModel> {

    private Long id;
    private String name;
    private Long parentId;
    private String username;

    public FolderModel(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
        this.parentId = folder.getParent() != null ? folder.getParent().getId() : null;
        this.username = folder.getUser().getUsername();
    }
}