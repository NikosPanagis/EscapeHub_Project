package com.ihu.escapehub.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class UserService {

    static final int MIN_PASSWORD_LENGTH = 6;
    static final int MAX_PASSWORD_BYTES = 72;
    static final String USERNAME_TAKEN = "Το username χρησιμοποιείται ήδη.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String password) {
        String cleanUsername = (username == null) ? "" : username.strip();

        if (cleanUsername.isEmpty()) {
            throw new IllegalArgumentException("Το username είναι υποχρεωτικό.");
        }

        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Ο κωδικός πρέπει να έχει τουλάχιστον " + MIN_PASSWORD_LENGTH + " χαρακτήρες.");
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new IllegalArgumentException("Ο κωδικός είναι πολύ μεγάλος.");
        }

        if (userRepository.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException(USERNAME_TAKEN);
        }

        String passwordHash = passwordEncoder.encode(password);
        try {
            return userRepository.saveAndFlush(new User(cleanUsername, passwordHash));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(USERNAME_TAKEN, e);
        }
    }
}