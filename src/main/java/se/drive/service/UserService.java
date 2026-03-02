package se.drive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import se.drive.model.User;
import se.drive.repository.UserRepository;

import java.time.LocalDateTime;

/**
 * Service för användarhantering.
 * Hanterar skapande och hämtning av användare från GitHub OAuth.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Hämtar eller skapar en användare baserat på GitHub OAuth-data.
     *
     * @param oauth2User GitHub-användardata från OAuth2
     * @return Användaren från databasen
     */
    public User getOrCreateUser(OAuth2User oauth2User) {
        String githubId = oauth2User.getAttribute("id").toString();

        return userRepository.findByGithubId(githubId)
                .orElseGet(() -> createUser(oauth2User));
    }

    /**
     * Skapar en ny användare från GitHub OAuth-data.
     *
     * @param oauth2User GitHub-användardata
     * @return Den skapade användaren
     */
    private User createUser(OAuth2User oauth2User) {
        User user = new User();
        user.setGithubId(oauth2User.getAttribute("id").toString());
        user.setUsername(oauth2User.getAttribute("login"));
        user.setEmail(oauth2User.getAttribute("email"));
        user.setAvatarUrl(oauth2User.getAttribute("avatar_url"));
        user.setCreatedAt(LocalDateTime.now().toString());

        return userRepository.save(user);
    }
}