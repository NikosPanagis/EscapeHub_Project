package com.ihu.escapehub.evidence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class EvidenceServiceTest {

    private static final Long TEAM_A = 1L;
    private static final Long TEAM_B = 2L;

    @Mock
    private EvidenceRepository evidenceRepository;

    private EvidenceService evidenceService;

    private final List<EvidenceEntry> stored = new ArrayList<>();

    @BeforeEach
    void setUp() {
        evidenceService = new EvidenceService(evidenceRepository);

        lenient().when(evidenceRepository.saveAndFlush(any(EvidenceEntry.class)))
                .thenAnswer(invocation -> {
                    EvidenceEntry entry = invocation.getArgument(0);
                    stored.add(entry);
                    return entry;
                });

        lenient().when(evidenceRepository.existsByTeamIdAndEvidenceKey(anyLong(), anyString()))
                .thenAnswer(invocation -> {
                    Long teamId = invocation.getArgument(0);
                    String key = invocation.getArgument(1);
                    return stored.stream().anyMatch(entry ->
                            entry.getTeamId().equals(teamId) && entry.getEvidenceKey().equals(key));
                });

        lenient().when(evidenceRepository.findByTeamIdOrderByFoundAtDesc(anyLong()))
                .thenAnswer(invocation -> {
                    Long teamId = invocation.getArgument(0);
                    return stored.stream()
                            .filter(entry -> entry.getTeamId().equals(teamId))
                            .sorted(Comparator.comparing(EvidenceEntry::getFoundAt).reversed())
                            .toList();
                });
    }

    @Test
    void addedEvidenceAppearsInTeamList() {
        boolean added = evidenceService.addEvidence(TEAM_A, "year", "Έτος πρώτης έκδοσης", "alice");

        assertTrue(added);
        List<EvidenceEntry> entries = evidenceService.listForTeam(TEAM_A);
        assertEquals(1, entries.size());
        assertEquals("year", entries.getFirst().getEvidenceKey());
        assertEquals("alice", entries.getFirst().getFoundBy());
    }

    @Test
    void duplicateEvidenceIsNotAddedTwice() {
        evidenceService.addEvidence(TEAM_A, "year", "Έτος πρώτης έκδοσης", "alice");

        boolean addedAgain = evidenceService.addEvidence(TEAM_A, "year", "Έτος πρώτης έκδοσης", "bob");

        assertFalse(addedAgain);
        assertEquals(1, evidenceService.listForTeam(TEAM_A).size());
    }

    @Test
    void teamDoesNotSeeEvidenceOfAnotherTeam() {
        evidenceService.addEvidence(TEAM_A, "year", "Έτος πρώτης έκδοσης", "alice");

        assertTrue(evidenceService.listForTeam(TEAM_B).isEmpty());
        assertEquals(1, evidenceService.listForTeam(TEAM_A).size());
    }

    @Test
    void sameEvidenceCanBeFoundByDifferentTeams() {
        evidenceService.addEvidence(TEAM_A, "year", "Έτος πρώτης έκδοσης", "alice");

        boolean addedForOtherTeam = evidenceService.addEvidence(TEAM_B, "year", "Έτος πρώτης έκδοσης", "carol");

        assertTrue(addedForOtherTeam);
        assertEquals(1, evidenceService.listForTeam(TEAM_A).size());
        assertEquals(1, evidenceService.listForTeam(TEAM_B).size());
    }

    @Test
    void unlockAddsEvidenceToTeamJournal() {
        evidenceService.unlock(TEAM_A, "city-dublin");

        List<EvidenceEntry> entries = evidenceService.listForTeam(TEAM_A);
        assertEquals(1, entries.size());
        assertEquals("city-dublin", entries.getFirst().getEvidenceKey());
    }

    @Test
    void blankEvidenceKeyIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> evidenceService.addEvidence(TEAM_A, "  ", "κάτι", "alice"));
    }

    @Test
    void missingTeamIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> evidenceService.addEvidence(null, "year", "κάτι", "alice"));
    }

    @Test
    void simultaneousDuplicateIsHandledWithoutError() {
        doThrow(new DataIntegrityViolationException("duplicate"))
                .when(evidenceRepository).saveAndFlush(any(EvidenceEntry.class));

        boolean added = evidenceService.addEvidence(TEAM_A, "year", "Έτος πρώτης έκδοσης", "alice");

        assertFalse(added);
    }
}
