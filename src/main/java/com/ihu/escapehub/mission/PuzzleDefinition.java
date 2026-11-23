package com.ihu.escapehub.mission;

public record PuzzleDefinition(
        String id,
        String type,
        String question,
        String correctAnswer,
        String evidenceKey
) {
}