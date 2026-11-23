package com.ihu.escapehub.puzzle;

public class TextAnswerPuzzle implements Puzzle {

    private final String id;
    private final String correctAnswer;

    public TextAnswerPuzzle(String id, String correctAnswer) {
        this.id = id;
        this.correctAnswer = correctAnswer;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean isCorrect(String answer) {
        if (answer == null || correctAnswer == null) {
            return false;
        }
        return correctAnswer.trim().equalsIgnoreCase(answer.trim());
    }
}