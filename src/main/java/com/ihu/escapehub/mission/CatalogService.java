package com.ihu.escapehub.mission;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class CatalogService {

    private final MissionLoader missionLoader;
    private final MissionRunRepository missionRunRepository;

    public CatalogService(MissionLoader missionLoader, MissionRunRepository missionRunRepository) {
        this.missionLoader = missionLoader;
        this.missionRunRepository = missionRunRepository;
    }

    public List<MissionDefinition> listMissions() {
        return missionLoader.findAll();
    }

    @Transactional
    public MissionRun start(Long teamId, String missionId) {
        if (missionLoader.findById(missionId).isEmpty()) {
            throw new IllegalArgumentException("Η αποστολή δεν υπάρχει: " + missionId);
        }

        return missionRunRepository.findByTeamIdAndMissionId(teamId, missionId)
                .orElseGet(() -> missionRunRepository.save(
                        new MissionRun(teamId, missionId, Instant.now())));
    }
}