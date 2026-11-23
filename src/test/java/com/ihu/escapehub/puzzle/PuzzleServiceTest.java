package com.ihu.escapehub.puzzle;

import com.ihu.escapehub.mission.MissionDefinition;
import com.ihu.escapehub.mission.MissionLoader;
import com.ihu.escapehub.mission.PathDefinition;
import com.ihu.escapehub.mission.PuzzleDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PuzzleServiceTest {

    @Mock
    private MissionLoader missionLoader;

    @Mock
    private SolvedPuzzleRepository repository;

    @Mock
    private EvidenceUnlocker evidenceUnlocker;

    private PuzzleService puzzleService;

    @BeforeEach
    void setUp() {
        puzzleService = new PuzzleService(missionLoader, repository, evidenceUnlocker, new PuzzleFactory());
    }

    private PuzzleDefinition sampleDefinition(String type) {
        return new PuzzleDefinition(
                "p1",
                type,
                "Ποιος έγραψε τον Δράκουλα;",
                "Bram Stoker",
                "diary_page_1"
        );
    }

    private void stubPuzzleInLoader(PuzzleDefinition puzzleDef) {
        PathDefinition path = new PathDefinition("path1", "Διαδρομή 1", List.of(puzzleDef));
        MissionDefinition mission = new MissionDefinition("m1", "Αποστολή 1", "Περιγραφή", List.of(path));
        when(missionLoader.findAll()).thenReturn(List.of(mission));
    }

    @Test
    void fullStateTransitionFromUnsolvedToSolved() {
        stubPuzzleInLoader(sampleDefinition("text"));
        AtomicBoolean solved = new AtomicBoolean(false);

        when(repository.existsByTeamIdAndPuzzleId(1L, "p1")).thenAnswer(inv -> solved.get());
        when(repository.save(any(SolvedPuzzle.class))).thenAnswer(inv -> {
            solved.set(true);
            return inv.getArgument(0);
        });

        // 1. Αρχική κατάσταση: UNSOLVED
        assertEquals(PuzzleStatus.UNSOLVED, puzzleService.status(1L, "p1"));

        // 2. Λάθος απάντηση: παραμένει UNSOLVED
        assertEquals(PuzzleStatus.UNSOLVED, puzzleService.submit(1L, "p1", "λάθος"));
        assertEquals(PuzzleStatus.UNSOLVED, puzzleService.status(1L, "p1"));
        verifyNoInteractions(evidenceUnlocker);

        // 3. Σωστή απάντηση: μεταβαίνει σε SOLVED και ξεκλειδώνει το στοιχείο
        assertEquals(PuzzleStatus.SOLVED, puzzleService.submit(1L, "p1", "Bram Stoker"));
        assertEquals(PuzzleStatus.SOLVED, puzzleService.status(1L, "p1"));
        verify(evidenceUnlocker, times(1)).unlock(1L, "diary_page_1");
    }

    @Test
    void wrongAnswerReturnsUnsolvedAndDoesNotUnlock() {
        stubPuzzleInLoader(sampleDefinition("text"));
        when(repository.existsByTeamIdAndPuzzleId(1L, "p1")).thenReturn(false);

        PuzzleStatus status = puzzleService.submit(1L, "p1", "λάθος");

        assertEquals(PuzzleStatus.UNSOLVED, status);
        verify(repository, never()).save(any());
        verifyNoInteractions(evidenceUnlocker);
    }

    @Test
    void correctAnswerSavesAndUnlocksEvidenceOnce() {
        stubPuzzleInLoader(sampleDefinition("text"));
        when(repository.existsByTeamIdAndPuzzleId(1L, "p1")).thenReturn(false);

        PuzzleStatus status = puzzleService.submit(1L, "p1", "bram stoker");

        assertEquals(PuzzleStatus.SOLVED, status);
        verify(repository, times(1)).save(any(SolvedPuzzle.class));
        verify(evidenceUnlocker, times(1)).unlock(1L, "diary_page_1");
    }

    @Test
    void alreadySolvedReturnsSolvedWithoutDuplicateUnlock() {
        stubPuzzleInLoader(sampleDefinition("text"));
        when(repository.existsByTeamIdAndPuzzleId(1L, "p1")).thenReturn(true);

        PuzzleStatus status = puzzleService.submit(1L, "p1", "Bram Stoker");

        assertEquals(PuzzleStatus.SOLVED, status);
        verify(repository, never()).save(any());
        verifyNoInteractions(evidenceUnlocker);
    }

    @Test
    void concurrentSubmissionHandlesConstraintViolationGracefully() {
        stubPuzzleInLoader(sampleDefinition("text"));
        when(repository.existsByTeamIdAndPuzzleId(1L, "p1")).thenReturn(false, true);
        when(repository.save(any(SolvedPuzzle.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        PuzzleStatus status = puzzleService.submit(1L, "p1", "Bram Stoker");

        assertEquals(PuzzleStatus.SOLVED, status);
        verifyNoInteractions(evidenceUnlocker);
    }

    @Test
    void genuineDatabaseConstraintViolationRethrowsException() {
        stubPuzzleInLoader(sampleDefinition("text"));
        when(repository.existsByTeamIdAndPuzzleId(1L, "p1")).thenReturn(false, false);
        when(repository.save(any(SolvedPuzzle.class)))
                .thenThrow(new DataIntegrityViolationException("Not null violation"));

        assertThrows(DataIntegrityViolationException.class, () ->
                puzzleService.submit(1L, "p1", "Bram Stoker")
        );
        verifyNoInteractions(evidenceUnlocker);
    }

    @Test
    void unknownPuzzleIdThrowsException() {
        stubPuzzleInLoader(sampleDefinition("text"));

        assertThrows(IllegalArgumentException.class, () ->
                puzzleService.submit(1L, "unknown_id", "Bram Stoker")
        );
        verifyNoInteractions(repository, evidenceUnlocker);
    }

    @Test
    void unknownPuzzleTypeThrowsException() {
        stubPuzzleInLoader(sampleDefinition("unknown_type"));

        assertThrows(IllegalArgumentException.class, () ->
                puzzleService.submit(1L, "p1", "test")
        );
        verifyNoInteractions(repository, evidenceUnlocker);
    }
}