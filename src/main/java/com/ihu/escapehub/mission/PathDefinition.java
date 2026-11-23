package com.ihu.escapehub.mission;

import java.util.List;

public record PathDefinition(
        String id,
        String title,
        List<PuzzleDefinition> puzzles
) {
}