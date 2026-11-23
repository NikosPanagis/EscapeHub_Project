package com.ihu.escapehub.puzzle;

import com.ihu.escapehub.mission.PuzzleDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleFactoryTest {

    private final PuzzleFactory factory = new PuzzleFactory();

    @Test
    void nullDefinitionThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    void nullTypeThrowsException() {
        PuzzleDefinition definition = new PuzzleDefinition("p1", null, "Ερώτηση;", "Απάντηση", "ev_1");
        assertThrows(IllegalArgumentException.class, () -> factory.create(definition));
    }

    @Test
    void textTypeWithSpacesAndUppercaseCreatesTextAnswerPuzzle() {
        PuzzleDefinition definition = new PuzzleDefinition("p1", " TEXT ", "Ερώτηση;", "Bram Stoker", "ev_1");

        Puzzle puzzle = factory.create(definition);

        assertInstanceOf(TextAnswerPuzzle.class, puzzle);
        assertTrue(puzzle.isCorrect("bram stoker"));
    }

    @Test
    void unknownTypeThrowsException() {
        PuzzleDefinition definition = new PuzzleDefinition("p1", "image", "Ερώτηση;", "Απάντηση", "ev_1");
        assertThrows(IllegalArgumentException.class, () -> factory.create(definition));
    }
}