package se.drive.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entitet som representerar en uppladdad fil.
 * Innehåller metadata om filen och referens till mappen den ligger i.
 */
@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Originalfilnamn från uppladdningen.
     */
    @Column(nullable = false)
    private String filename;

    /**
     * MIME-typ (t.ex. "application/pdf").
     */
    @Column(nullable = false)
    private String contentType;

    /**
     * Filstorlek i bytes.
     */
    @Column(nullable = false)
    private long size;

    /**
     * Sökväg till filen på disk.
     */
    @Column(nullable = false)
    private String storagePath;

    /**
     * Mappen som filen tillhör.
     */
    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;
}