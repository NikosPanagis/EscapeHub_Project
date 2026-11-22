package com.ihu.escapehub.puzzle;

import org.springframework.stereotype.Component;

@Component
public class StubEvidenceUnlocker implements EvidenceUnlocker {

    @Override
    public void unlock(Long teamId, String evidenceKey) {
        System.out.println("Ξεκλειδώθηκε το στοιχείο '" + evidenceKey + "' για την ομάδα " + teamId);
    }
}