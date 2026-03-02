package se.drive.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String githubId;  // GitHub's user ID

    @Column(nullable = false)
    private String username;  // GitHub username (login)

    private String email;

    private String avatarUrl;

    @Column(nullable = false)
    private String createdAt;
}