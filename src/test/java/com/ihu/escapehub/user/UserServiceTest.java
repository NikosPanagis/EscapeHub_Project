package com.ihu.escapehub.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Unit tests for the registration rules. Repository and encoder are mocks: no database needed. */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    /** Sets up the "happy path": encoding works and saveAndFlush returns the user it was given. */
    private void stubSuccessfulSave() {
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // --- Tests required by the Sprint 1 guide ---

    @Test
    void newUsernameIsSaved() {
        stubSuccessfulSave();

        User saved = userService.register("alice", "secret1");

        verify(userRepository).saveAndFlush(any(User.class));
        assertEquals("alice", saved.getUsername());
    }

    @Test
    void existingUsernameIsRejected() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", "secret1"));

        assertEquals(UserService.USERNAME_TAKEN, e.getMessage());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void passwordIsNotStoredAsGiven() {
        stubSuccessfulSave();

        userService.register("alice", "secret1");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captor.capture());
        assertNotEquals("secret1", captor.getValue().getPasswordHash());
        assertEquals("hashed", captor.getValue().getPasswordHash());
    }

    // --- Username rules ---

    @Test
    void usernameIsStripped() {
        stubSuccessfulSave();

        User saved = userService.register("  alice  ", "secret1");

        assertEquals("alice", saved.getUsername());
        verify(userRepository).existsByUsername("alice");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void blankUsernameIsRejected(String username) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.register(username, "secret1"));

        assertEquals("Το username είναι υποχρεωτικό.", e.getMessage());
        verify(userRepository, never()).saveAndFlush(any());
    }

    // --- Password length rules ---

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"12345"})
    void tooShortPasswordIsRejected(String password) {
        assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", password));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void passwordWithExactlyMinimumLengthIsAccepted() {
        stubSuccessfulSave();

        userService.register("alice", "a".repeat(UserService.MIN_PASSWORD_LENGTH));

        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void passwordOf72BytesIsAccepted() {
        stubSuccessfulSave();

        userService.register("alice", "a".repeat(UserService.MAX_PASSWORD_BYTES));

        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void passwordOver72BytesIsRejected() {
        String password = "a".repeat(UserService.MAX_PASSWORD_BYTES + 1);

        assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", password));

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void greekPasswordIsMeasuredInBytesNotCharacters() {
        // 40 Greek letters = 40 characters (allowed by length) but 80 bytes (over the limit)
        String password = "α".repeat(40);

        assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", password));
    }

    // --- Two registrations with the same name at the same time ---

    @Test
    void duplicateCaughtByDatabaseIsReportedAsTaken() {
        // existsByUsername returns false (default), as if the other user was not saved yet
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", "secret1"));

        assertEquals(UserService.USERNAME_TAKEN, e.getMessage());
    }
}