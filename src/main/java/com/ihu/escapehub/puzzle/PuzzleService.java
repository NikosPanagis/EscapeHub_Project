package com.ihu.escapehub.puzzle;

import com.ihu.escapehub.mission.MissionDefinition;
import com.ihu.escapehub.mission.MissionLoader;
import com.ihu.escapehub.mission.PathDefinition;
import com.ihu.escapehub.mission.PuzzleDefinition;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PuzzleService {

    private static final Logger log = LoggerFactory.getLogger(PuzzleService.class);

    private final MissionLoader missionLoader;
    private final SolvedPuzzleRepository solvedPuzzleRepository;

    public PuzzleService(MissionLoader missionLoader, SolvedPuzzleRepository solvedPuzzleRepository) {
        this.missionLoader = missionLoader;
        this.solvedPuzzleRepository = solvedPuzzleRepository;
    }

    public PuzzleStatus status(Long teamId, String puzzleId) {
        if (solvedPuzzleRepository.existsByTeamIdAndPuzzleId(teamId, puzzleId)) {
            return PuzzleStatus.SOLVED;
        }
        return PuzzleStatus.UNSOLVED;
    }

    public PuzzleStatus submit(Long teamId, String puzzleId, String answer) {
        PuzzleDefinition definition = findPuzzleDefinition(puzzleId)
                .orElseThrow(() -> new IllegalArgumentException("Ο γρίφος δεν βρέθηκε: " + puzzleId));

        Puzzle puzzle = new TextAnswerPuzzle(definition.id(), definition.correctAnswer());

        if (!puzzle.isCorrect(answer)) {
            return status(teamId, puzzleId);
        }

        if (!solvedPuzzleRepository.existsByTeamIdAndPuzzleId(teamId, puzzleId)) {
            solvedPuzzleRepository.save(new SolvedPuzzle(teamId, puzzleId, LocalDateTime.now()));
            notifyEvidenceService(teamId, definition);
        }

        return PuzzleStatus.SOLVED;
    }

    public Optional<PuzzleDefinition> findPuzzleDefinition(String puzzleId) {
        for (MissionDefinition mission : missionLoader.findAll()) {
            if (mission.paths() == null) continue;
            for (PathDefinition path : mission.paths()) {
                if (path.puzzles() == null) continue;
                for (PuzzleDefinition puzzle : path.puzzles()) {
                    if (puzzle.id().equals(puzzleId)) {
                        return Optional.of(puzzle);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public Optional<PuzzleDefinition> getFirstAvailablePuzzle() {
        for (MissionDefinition mission : missionLoader.findAll()) {
            if (mission.paths() == null) continue;
            for (PathDefinition path : mission.paths()) {
                if (path.puzzles() != null && !path.puzzles().isEmpty()) {
                    return Optional.of(path.puzzles().getFirst());
                }
            }
        }
        return Optional.empty();
    }

    protected void notifyEvidenceService(Long teamId, PuzzleDefinition definition) {
        log.info("Ξεκλειδώθηκε το στοιχείο '{}' για την ομάδα {} από τον γρίφο {}",
                definition.evidenceKey(), teamId, definition.id());
    }
}