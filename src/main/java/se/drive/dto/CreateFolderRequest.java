package se.drive.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO för att skapa en ny mapp.
 */
@Getter
@Setter
public class CreateFolderRequest {

    /**
     * Namn på mappen som ska skapas.
     */
    private String name;

    /**
     * ID för föräldramappen (null om det är en rotmapp).
     */
    private Long parentId;
}