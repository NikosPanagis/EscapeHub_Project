package com.ihu.escapehub.puzzle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "solved_puzzles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "puzzle_id"})
)
public class SolvedPuzzle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "puzzle_id", nullable = false)
    private String puzzleId;

    @Column(name = "solved_at", nullable = false)
    private LocalDateTime solvedAt;

    protected SolvedPuzzle() {
    }

    public SolvedPuzzle(Long teamId, String puzzleId, LocalDateTime solvedAt) {
        this.teamId = teamId;
        this.puzzleId = puzzleId;
        this.solvedAt = solvedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getPuzzleId() {
        return puzzleId;
    }

    public LocalDateTime getSolvedAt() {
        return solvedAt;
    }
}