package com.ihu.escapehub.puzzle;

import com.ihu.escapehub.mission.PuzzleDefinition;
import org.springframework.stereotype.Component;

@Component
public class PuzzleFactory {

    public Puzzle create(PuzzleDefinition definition) {
        if (definition == null || definition.type() == null) {
            throw new IllegalArgumentException("Ο ορισμός ή ο τύπος του γρίφου δεν μπορεί να είναι κενός");
        }

        return switch (definition.type().trim().toLowerCase()) {
            case "text" -> new TextAnswerPuzzle(
                    definition.id(),
                    definition.correctAnswer()
            );
            default -> throw new IllegalArgumentException("Άγνωστος τύπος γρίφου: " + definition.type());
        };
    }
}