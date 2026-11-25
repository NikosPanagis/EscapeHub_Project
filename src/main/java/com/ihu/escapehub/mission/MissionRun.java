package com.ihu.escapehub.mission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class MissionRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private String missionId;

    @Column(nullable = false)
    private Instant startedAt;

    protected MissionRun() {
    }

    public MissionRun(Long teamId, String missionId, Instant startedAt) {
        this.teamId = teamId;
        this.missionId = missionId;
        this.startedAt = startedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getMissionId() {
        return missionId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }
}
