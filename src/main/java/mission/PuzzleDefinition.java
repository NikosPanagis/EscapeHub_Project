package mission;

import java.util.List;

public record PuzzleDefinition(
        String id,
        String type,
        String question,
        String correctAnswer,
        String evidenceKey
) {
}
