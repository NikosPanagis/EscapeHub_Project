package com.ihu.escapehub.puzzle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TextAnswerPuzzleTest {

    @Test
    void returnsTrueForExactCorrectAnswer() {
        Puzzle puzzle = new TextAnswerPuzzle("p1", "Frankenstein");
        assertTrue(puzzle.isCorrect("Frankenstein"));
    }

    @Test
    void returnsFalseForWrongAnswer() {
        Puzzle puzzle = new TextAnswerPuzzle("p1", "Frankenstein");
        assertFalse(puzzle.isCorrect("Dracula"));
    }

    @Test
    void ignoresLeadingTrailingSpacesAndCase() {
        Puzzle puzzle = new TextAnswerPuzzle("p1", "Frankenstein");
        assertTrue(puzzle.isCorrect("   fRaNkEnStEiN  "));
    }

    @Test
    void returnsFalseForNullAnswer() {
        Puzzle puzzle = new TextAnswerPuzzle("p1", "Frankenstein");
        assertFalse(puzzle.isCorrect(null));
    }
}