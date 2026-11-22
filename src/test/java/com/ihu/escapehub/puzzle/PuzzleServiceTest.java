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
        PathDefinition path = mock(PathDefinition.class);
        when(path.puzzles()).thenReturn(List.of(puzzleDef));

        MissionDefinition mission = mock(MissionDefinition.class);
        when(mission.paths()).thenReturn(List.of(path));

        when(missionLoader.findAll()).thenReturn(List.of(mission));
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
        when(repository.existsByTeamIdAndPuzzleId(1L, "p1")).thenReturn(false);
        when(repository.save(any(SolvedPuzzle.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key"));

        PuzzleStatus status = puzzleService.submit(1L, "p1", "Bram Stoker");

        assertEquals(PuzzleStatus.SOLVED, status);
        verifyNoInteractions(evidenceUnlocker);
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