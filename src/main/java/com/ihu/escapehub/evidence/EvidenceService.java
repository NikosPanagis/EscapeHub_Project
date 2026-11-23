package com.ihu.escapehub.evidence;

import com.ihu.escapehub.puzzle.EvidenceUnlocker;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class EvidenceService implements EvidenceUnlocker {

    private final EvidenceRepository evidenceRepository;

    public EvidenceService(EvidenceRepository evidenceRepository) {
        this.evidenceRepository = evidenceRepository;
    }

    public boolean addEvidence(Long teamId, String evidenceKey, String description, String foundBy) {
        if (teamId == null) {
            throw new IllegalArgumentException("Η ομάδα είναι υποχρεωτική.");
        }
        if (evidenceKey == null || evidenceKey.isBlank()) {
            throw new IllegalArgumentException("Το στοιχείο είναι υποχρεωτικό.");
        }

        String cleanKey = evidenceKey.strip();
        if (evidenceRepository.existsByTeamIdAndEvidenceKey(teamId, cleanKey)) {
            return false;
        }

        EvidenceEntry entry = new EvidenceEntry(teamId, cleanKey, description, foundBy, LocalDateTime.now());
        try {
            evidenceRepository.saveAndFlush(entry);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Δύο μέλη πρόσθεσαν το ίδιο στοιχείο ταυτόχρονα: η βάση κράτησε το πρώτο.
            return false;
        }
    }

    @Override
    public void unlock(Long teamId, String evidenceKey) {
        addEvidence(teamId, evidenceKey, null, null);
    }

    public List<EvidenceEntry> listForTeam(Long teamId) {
        if (teamId == null) {
            return List.of();
        }
        return evidenceRepository.findByTeamIdOrderByFoundAtDesc(teamId);
    }
}
