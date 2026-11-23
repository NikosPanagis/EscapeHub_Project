package com.ihu.escapehub.puzzle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// TODO: delete when EvidenceService (Μέλος 5) is merged
@Component
public class StubEvidenceUnlocker implements EvidenceUnlocker {

    private static final Logger log = LoggerFactory.getLogger(StubEvidenceUnlocker.class);

    @Override
    public void unlock(Long teamId, String evidenceKey) {
        log.info("Stub: ξεκλειδώθηκε το στοιχείο '{}' για την ομάδα {}", evidenceKey, teamId);
    }
}