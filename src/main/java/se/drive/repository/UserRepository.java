package se.drive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.drive.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGithubId(String githubId);
}