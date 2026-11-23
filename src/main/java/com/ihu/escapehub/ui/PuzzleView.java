package com.ihu.escapehub.ui;

import com.ihu.escapehub.mission.PuzzleDefinition;
import com.ihu.escapehub.puzzle.PuzzleService;
import com.ihu.escapehub.puzzle.PuzzleStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@PermitAll
@Route("puzzle")
public class PuzzleView extends VerticalLayout {

    public PuzzleView(PuzzleService puzzleService) {
        setMaxWidth("600px");
        setSpacing(true);
        setPadding(true);

        add(new H2("Επίλυση Γρίφου"));

        Optional<PuzzleDefinition> puzzleOpt = puzzleService.getFirstAvailablePuzzle();
        if (puzzleOpt.isEmpty()) {
            add(new Paragraph("Δεν βρέθηκε διαθέσιμος γρίφος."));
            return;
        }

        PuzzleDefinition currentPuzzle = puzzleOpt.get();
        add(new Paragraph(currentPuzzle.question()));

        TextField answerField = new TextField("Η απάντησή σου");
        answerField.setWidthFull();

        Button submitButton = new Button("Απάντηση", event -> {
            // TODO: αντικατάσταση του hardcoded teamId με το teamId της ομάδας του συνδεδεμένου χρήστη
            Long teamId = 1L;
            PuzzleStatus result = puzzleService.submit(teamId, currentPuzzle.id(), answerField.getValue());

            if (result == PuzzleStatus.SOLVED) {
                Notification notification = Notification.show("Σωστό!", 3000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification notification = Notification.show("Δοκίμασε ξανά", 3000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        add(answerField, submitButton);
    }
}