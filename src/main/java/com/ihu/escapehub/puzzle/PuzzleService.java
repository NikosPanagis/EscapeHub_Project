package com.ihu.escapehub.puzzle;

import com.ihu.escapehub.mission.MissionDefinition;
import com.ihu.escapehub.mission.MissionLoader;
import com.ihu.escapehub.mission.PathDefinition;
import com.ihu.escapehub.mission.PuzzleDefinition;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PuzzleService {

    private final MissionLoader missionLoader;
    private final SolvedPuzzleRepository solvedPuzzleRepository;
    private final EvidenceUnlocker evidenceUnlocker;
    private final PuzzleFactory puzzleFactory;

    public PuzzleService(MissionLoader missionLoader,
                         SolvedPuzzleRepository solvedPuzzleRepository,
                         EvidenceUnlocker evidenceUnlocker,
                         PuzzleFactory puzzleFactory) {
        this.missionLoader = missionLoader;
        this.solvedPuzzleRepository = solvedPuzzleRepository;
        this.evidenceUnlocker = evidenceUnlocker;
        this.puzzleFactory = puzzleFactory;
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

        Puzzle puzzle = puzzleFactory.create(definition);

        if (!puzzle.isCorrect(answer)) {
            return status(teamId, puzzleId);
        }

        if (!solvedPuzzleRepository.existsByTeamIdAndPuzzleId(teamId, puzzleId)) {
            try {
                solvedPuzzleRepository.save(new SolvedPuzzle(teamId, puzzleId, LocalDateTime.now()));
            } catch (DataIntegrityViolationException e) {
                return PuzzleStatus.SOLVED;
            }
            evidenceUnlocker.unlock(teamId, definition.evidenceKey());
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
}