package com.ihu.escapehub.mission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionRunRepository extends JpaRepository<MissionRun, Long> {

    Optional<MissionRun> findByTeamIdAndMissionId(Long teamId, String missionId);
}