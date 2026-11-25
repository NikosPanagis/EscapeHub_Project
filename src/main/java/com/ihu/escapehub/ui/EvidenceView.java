package com.ihu.escapehub.ui;

import com.ihu.escapehub.evidence.EvidenceEntry;
import com.ihu.escapehub.evidence.EvidenceService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Το κοινό ημερολόγιο στοιχείων της ομάδας. Ανοίγει μόνο σε συνδεδεμένο χρήστη. */
@PermitAll
@Route("evidence")
@PageTitle("Ημερολόγιο στοιχείων")
public class EvidenceView extends VerticalLayout {

    // TODO: όταν μπουν οι ομάδες (Μέλος 2), η ομάδα θα έρχεται από τον συνδεδεμένο χρήστη.
    private static final Long TEMPORARY_TEAM_ID = 1L;

    private static final DateTimeFormatter FOUND_AT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EvidenceView(EvidenceService evidenceService) {
        add(new H2("Ημερολόγιο στοιχείων"));

        List<EvidenceEntry> entries = evidenceService.listForTeam(TEMPORARY_TEAM_ID);
        if (entries.isEmpty()) {
            add(new Paragraph("Η ομάδα δεν έχει βρει ακόμα στοιχεία."));
            return;
        }

        Grid<EvidenceEntry> grid = new Grid<>(EvidenceEntry.class, false);
        grid.addColumn(EvidenceEntry::getEvidenceKey).setHeader("Στοιχείο");
        grid.addColumn(EvidenceEntry::getDescription).setHeader("Περιγραφή");
        grid.addColumn(EvidenceEntry::getFoundBy).setHeader("Ποιος");
        grid.addColumn(entry -> entry.getFoundAt().format(FOUND_AT_FORMAT)).setHeader("Πότε");
        grid.setItems(entries);
        add(grid);
    }
}
