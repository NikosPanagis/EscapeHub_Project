package com.ihu.escapehub.evidence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvidenceRepository extends JpaRepository<EvidenceEntry, Long> {

    List<EvidenceEntry> findByTeamIdOrderByFoundAtDesc(Long teamId);

    boolean existsByTeamIdAndEvidenceKey(Long teamId, String evidenceKey);
}
