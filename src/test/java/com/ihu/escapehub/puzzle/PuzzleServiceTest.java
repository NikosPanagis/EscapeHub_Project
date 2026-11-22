package com.ihu.escapehub.puzzle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ihu.escapehub.mission.MissionDefinition;
import com.ihu.escapehub.mission.MissionLoader;
import com.ihu.escapehub.mission.PathDefinition;
import com.ihu.escapehub.mission.PuzzleDefinition;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PuzzleServiceTest {

    @Mock
    private MissionLoader missionLoader;

    @Mock
    private SolvedPuzzleRepository solvedPuzzleRepository;

    private PuzzleService puzzleService;

    @BeforeEach
    void setUp() {
        puzzleService = spy(new PuzzleService(missionLoader, solvedPuzzleRepository));

        PuzzleDefinition puzzleDef = new PuzzleDefinition(
                "p1", "text", "Ποιο έργο γράφτηκε στη Βίλα Ντιοντάτι;", "Frankenstein", "clue-book"
        );
        PathDefinition pathDef = new PathDefinition("path1", "Διαδρομή 1", List.of(puzzleDef));
        MissionDefinition missionDef = new MissionDefinition("m1", "Villa Diodati", "Περιγραφή", List.of(pathDef));

        when(missionLoader.findAll()).thenReturn(List.of(missionDef));
    }

    @Test
    void stateTransitionsAndNoDuplicateEvidenceOnSecondCorrectSubmit() {
        Long teamId = 1L;
        String puzzleId = "p1";

        // 1. Αρχικά είναι UNSOLVED
        when(solvedPuzzleRepository.existsByTeamIdAndPuzzleId(teamId, puzzleId)).thenReturn(false);
        assertEquals(PuzzleStatus.UNSOLVED, puzzleService.status(teamId, puzzleId));

        // 2. Λάθος απάντηση -> παραμένει UNSOLVED και δεν αποθηκεύεται
        PuzzleStatus afterWrong = puzzleService.submit(teamId, puzzleId, "Λάθος απάντηση");
        assertEquals(PuzzleStatus.UNSOLVED, afterWrong);
        verify(solvedPuzzleRepository, never()).save(any());
        verify(puzzleService, never()).notifyEvidenceService(any(), any());

        // 3. Πρώτη σωστή απάντηση -> γίνεται SOLVED και καταγράφεται 1 φορά
        PuzzleStatus afterFirstCorrect = puzzleService.submit(teamId, puzzleId, "  frankenstein ");
        assertEquals(PuzzleStatus.SOLVED, afterFirstCorrect);
        verify(solvedPuzzleRepository, times(1)).save(any(SolvedPuzzle.class));
        verify(puzzleService, times(1)).notifyEvidenceService(any(), any());

        // 4. Δεύτερη σωστή απάντηση -> παραμένει SOLVED χωρίς διπλή καταγραφή
        when(solvedPuzzleRepository.existsByTeamIdAndPuzzleId(teamId, puzzleId)).thenReturn(true);
        PuzzleStatus afterSecondCorrect = puzzleService.submit(teamId, puzzleId, "Frankenstein");
        assertEquals(PuzzleStatus.SOLVED, afterSecondCorrect);
        verify(solvedPuzzleRepository, times(1)).save(any(SolvedPuzzle.class));
        verify(puzzleService, times(1)).notifyEvidenceService(any(), any());
    }
}