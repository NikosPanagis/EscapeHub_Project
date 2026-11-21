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
        // Rule 1: "alice " and "alice" must be the same user
        String cleanUsername = (username == null) ? "" : username.strip();

        // Rule 2: cheap checks first, before touching the database
        if (cleanUsername.isEmpty()) {
            throw new IllegalArgumentException("Το username είναι υποχρεωτικό.");
        }

        // Rule 3: the password is NOT stripped, spaces may be intentional
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Ο κωδικός πρέπει να έχει τουλάχιστον " + MIN_PASSWORD_LENGTH + " χαρακτήρες.");
        }
        // Count bytes, not characters (a Greek letter is 2 bytes in UTF-8)
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new IllegalArgumentException("Ο κωδικός είναι πολύ μεγάλος.");
        }

        // Rule 4: one database query, only after the input is valid
        if (userRepository.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException(USERNAME_TAKEN);
        }

        // Rule 5: only the hash is ever stored
        String passwordHash = passwordEncoder.encode(password);
        try {
            // saveAndFlush sends the INSERT now, inside this try block
            return userRepository.saveAndFlush(new User(cleanUsername, passwordHash));
        } catch (DataIntegrityViolationException e) {
            // Someone else registered the same name between our check and our insert
            throw new IllegalArgumentException(USERNAME_TAKEN, e);
        }
    }
}