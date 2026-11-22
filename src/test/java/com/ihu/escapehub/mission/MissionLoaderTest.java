package com.ihu.escapehub.mission;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MissionLoaderTest {

    @Test
    void loadsWhitbyMissionFromJson() throws IOException {
        MissionLoader loader = new MissionLoader(new ObjectMapper());
        List<MissionDefinition> missions = loader.findAll();

        assertFalse(missions.isEmpty(), "Πρέπει να φορτωθεί τουλάχιστον μία αποστολή");

        MissionDefinition mission = loader.findById("whitby-manuscript").orElseThrow();
        assertEquals("Το Χαμένο Χειρόγραφο του Whitby", mission.title());
        assertFalse(mission.paths().isEmpty());
        assertEquals("Το βιβλίο", mission.paths().getFirst().title());
        assertFalse(mission.paths().getFirst().puzzles().isEmpty());
        assertEquals("A1", mission.paths().getFirst().puzzles().getFirst().id());
    }

    @Test
    void returnsEmptyForUnknownId() throws IOException {
        MissionLoader loader = new MissionLoader(new ObjectMapper());
        assertTrue(loader.findById("unknown").isEmpty());
    }
}