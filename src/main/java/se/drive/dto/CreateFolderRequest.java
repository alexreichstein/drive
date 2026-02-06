package se.drive.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFolderRequest {

    private String name;
    private Long parentId;
}