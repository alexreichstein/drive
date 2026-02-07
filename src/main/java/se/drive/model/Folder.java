package se.drive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entitet som representerar en mapp.
 */
@Entity
@Table(name = "folders")
@Getter
@Setter
@NoArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mappens namn.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Föräldramapp (null om det är en rotmapp).
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Folder parent;

    /**
     * Undermappar till denna mapp.
     * @JsonIgnore Behövs för att förhindra oändlig loop
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Folder> subfolders = new ArrayList<>();

    /**
     * Filer i denna mapp.
     * @JsonIgnore Behövs för att förhindra oändlig loop
     */
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FileEntity> files = new ArrayList<>();
}