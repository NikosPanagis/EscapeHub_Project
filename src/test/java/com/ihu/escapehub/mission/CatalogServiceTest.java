package com.ihu.escapehub.mission;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private MissionLoader missionLoader;

    @Mock
    private MissionRunRepository missionRunRepository;

    private MissionDefinition sampleMission() {
        return new MissionDefinition("m1", "Τίτλος", "Περιγραφή", List.of());
    }

    @Test
    void startCreatesMissionRunWhenNotStartedBefore() {
        CatalogService service = new CatalogService(missionLoader, missionRunRepository);
        when(missionLoader.findById("m1")).thenReturn(Optional.of(sampleMission()));
        when(missionRunRepository.findByTeamIdAndMissionId(1L, "m1")).thenReturn(Optional.empty());
        when(missionRunRepository.save(any(MissionRun.class))).thenAnswer(inv -> inv.getArgument(0));

        MissionRun run = service.start(1L, "m1");

        assertEquals(1L, run.getTeamId());
        assertEquals("m1", run.getMissionId());
        verify(missionRunRepository).save(any(MissionRun.class));
    }

    @Test
    void secondStartDoesNotCreateAnotherRun() {
        CatalogService service = new CatalogService(missionLoader, missionRunRepository);
        MissionRun existing = new MissionRun(1L, "m1", Instant.now());
        when(missionLoader.findById("m1")).thenReturn(Optional.of(sampleMission()));
        when(missionRunRepository.findByTeamIdAndMissionId(1L, "m1")).thenReturn(Optional.of(existing));

        MissionRun run = service.start(1L, "m1");

        assertSame(existing, run);
        verify(missionRunRepository, never()).save(any(MissionRun.class));
    }

    @Test
    void startUnknownMissionThrows() {
        CatalogService service = new CatalogService(missionLoader, missionRunRepository);
        when(missionLoader.findById("άγνωστη")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.start(1L, "άγνωστη"));
        verify(missionRunRepository, never()).save(any(MissionRun.class));
    }
}