package com.ihu.escapehub.puzzle;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SolvedPuzzleRepository extends JpaRepository<SolvedPuzzle, Long> {
    boolean existsByTeamIdAndPuzzleId(Long teamId, String puzzleId);
}
