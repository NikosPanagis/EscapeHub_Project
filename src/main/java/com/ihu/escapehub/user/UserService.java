package com.ihu.escapehub.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    static final int MIN_PASSWORD_LENGTH = 6;

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

        if (userRepository.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException("Το username χρησιμοποιείται ήδη.");
        }

        String passwordHash = passwordEncoder.encode(password);
        return userRepository.save(new User(cleanUsername, passwordHash));
    }
}