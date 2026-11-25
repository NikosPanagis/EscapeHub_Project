package com.ihu.escapehub.api;

import com.ihu.escapehub.mission.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MissionController {

    private final CatalogService catalogService;

    public MissionController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/missions")
    public List<MissionSummary> missions() {
        return catalogService.listMissions().stream()
                .map(m -> new MissionSummary(m.id(), m.title(), m.description()))
                .toList();
    }

    public record MissionSummary(String id, String title, String description) {
    }
}