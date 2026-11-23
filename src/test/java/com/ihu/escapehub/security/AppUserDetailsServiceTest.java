package com.ihu.escapehub.security;

import com.ihu.escapehub.user.User;
import com.ihu.escapehub.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService service;

    @Test
    void existingUserIsLoadedWithStoredHashAndUserRole() {
        when(userRepository.findByUsername("nikos"))
                .thenReturn(Optional.of(new User("nikos", "$2a$10$storedHash")));

        UserDetails details = service.loadUserByUsername("nikos");

        assertEquals("nikos", details.getUsername());
        assertEquals("$2a$10$storedHash", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void spacesAroundUsernameAreIgnoredLikeInRegistration() {
        when(userRepository.findByUsername("nikos"))
                .thenReturn(Optional.of(new User("nikos", "hash")));

        assertEquals("nikos", service.loadUserByUsername("  nikos ").getUsername());
    }

    @Test
    void unknownUsernameThrowsUsernameNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("ghost"));
    }
}