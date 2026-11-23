package com.ihu.escapehub.mission;

import java.util.List;

public record MissionDefinition(
        String id,
        String title,
        String description,
        List<PathDefinition> paths
) {
}