package com.ihu.escapehub.team;

import com.ihu.escapehub.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(teamRepository);
    }

    @Test
    void createTeam_addsCreatorAsMember() {
        User creator = new User("giorgos", "hash123");
        when(teamRepository.existsByName("Explorers")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        Team created = teamService.createTeam("Explorers", creator);

        assertEquals("Explorers", created.getName());
        assertEquals(creator, created.getCreator());
        assertTrue(created.getMembers().contains(creator));
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void createTeam_duplicateName_throwsException() {
        User creator = new User("giorgos", "hash123");
        when(teamRepository.existsByName("Explorers")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                teamService.createTeam("Explorers", creator));
        verify(teamRepository, never()).save(any());
    }

    @Test
    void joinTeam_addsNewMember() {
        User creator = new User("giorgos", "hash123");
        User friend = new User("maria", "hash456");
        Team team = new Team("Explorers", creator);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        teamService.joinTeam(1L, friend);

        assertTrue(team.getMembers().contains(friend));
        verify(teamRepository).save(team);
    }

    @Test
    void joinTeam_alreadyMember_throwsException() {
        User creator = new User("giorgos", "hash123");
        Team team = new Team("Explorers", creator);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        assertThrows(IllegalArgumentException.class, () ->
                teamService.joinTeam(1L, creator));
        verify(teamRepository, never()).save(any());
    }
}