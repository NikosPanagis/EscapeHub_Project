package mission;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MissionLoaderTest {

    @Test
    void loadsWhitbyMissionFromJson() throws IOException {
        // Arrange & Act: Φορτώνουμε τις αποστολές από τα resources
        MissionLoader loader = new MissionLoader(new ObjectMapper());
        List<MissionDefinition> missions = loader.findAll();

        // Assert: Ελέγχουμε ότι ήρθαν σωστά τίτλος, διαδρομή και γρίφοι
        assertFalse(missions.isEmpty(), "Πρέπει να φορτωθεί τουλάχιστον μία αποστολή");

        MissionDefinition mission = loader.findById("whitby-manuscript").orElseThrow();
        assertEquals("Το Χαμένο Χειρόγραφο του Whitby", mission.title());
        assertEquals(1, mission.paths().size());
        assertEquals("Το βιβλίο", mission.paths().getFirst().title());
        assertEquals(2, mission.paths().getFirst().puzzles().size());
        assertEquals("A1", mission.paths().getFirst().puzzles().getFirst().id());
    }
}