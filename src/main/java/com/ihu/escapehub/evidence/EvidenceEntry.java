package com.ihu.escapehub.evidence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "evidence_entries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "evidence_key"})
)
public class EvidenceEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "evidence_key", nullable = false)
    private String evidenceKey;

    @Column(name = "description")
    private String description;

    @Column(name = "found_by")
    private String foundBy;

    @Column(name = "found_at", nullable = false)
    private LocalDateTime foundAt;

    protected EvidenceEntry() {
    }

    public EvidenceEntry(Long teamId, String evidenceKey, String description,
                         String foundBy, LocalDateTime foundAt) {
        this.teamId = teamId;
        this.evidenceKey = evidenceKey;
        this.description = description;
        this.foundBy = foundBy;
        this.foundAt = foundAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getEvidenceKey() {
        return evidenceKey;
    }

    public String getDescription() {
        return description;
    }

    public String getFoundBy() {
        return foundBy;
    }

    public LocalDateTime getFoundAt() {
        return foundAt;
    }
}
