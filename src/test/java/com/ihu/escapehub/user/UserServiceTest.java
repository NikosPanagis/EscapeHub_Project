package com.ihu.escapehub.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

/** Unit tests: repository and encoder are mocks, so no database is needed. */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private void stubSuccessfulSave() {
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void newUsernameIsSaved() {
        stubSuccessfulSave();

        User saved = userService.register("alice", "secret1");

        verify(userRepository).saveAndFlush(any(User.class));
        assertEquals("alice", saved.getUsername());
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

    @Test
    void existingUsernameIsRejected() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", "secret1"));

        assertEquals(UserService.USERNAME_TAKEN, e.getMessage());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void concurrentDuplicateCaughtByDatabaseIsReportedAsTaken() {
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", "secret1"));

        assertEquals(UserService.USERNAME_TAKEN, e.getMessage());
    }

    @Test
    void usernameIsTrimmed() {
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

        assertEquals(UserService.USERNAME_REQUIRED, e.getMessage());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void tooLongUsernameIsRejected() {
        String username = "a".repeat(UserService.MAX_USERNAME_LENGTH + 1);

        assertThrows(IllegalArgumentException.class,
                () -> userService.register(username, "secret1"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"12345"})
    void tooShortPasswordIsRejected(String password) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", password));

        assertEquals(UserService.PASSWORD_TOO_SHORT, e.getMessage());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void passwordWithExactlyMinimumLengthIsAccepted() {
        stubSuccessfulSave();

        userService.register("alice", "123456");

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

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", password));

        assertEquals(UserService.PASSWORD_TOO_LONG, e.getMessage());
    }

    @Test
    void greekPasswordIsMeasuredInBytesNotCharacters() {
        // 40 Greek letters = 40 characters but 80 bytes in UTF-8
        String password = "α".repeat(40);

        assertThrows(IllegalArgumentException.class,
                () -> userService.register("alice", password));
    }
}
