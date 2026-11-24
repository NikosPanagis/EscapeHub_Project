package com.ihu.escapehub.ui;

import com.ihu.escapehub.team.Team;
import com.ihu.escapehub.team.TeamService;
import com.ihu.escapehub.user.User;
import com.ihu.escapehub.user.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UserDetails;

@Route("teams")
@PageTitle("Ομάδες | EscapeHub")
@PermitAll
public class TeamView extends VerticalLayout {

    private final TeamService teamService;
    private final User currentUser;
    private final Grid<Team> myTeamsGrid = new Grid<>(Team.class, false);
    private final Grid<Team> allTeamsGrid = new Grid<>(Team.class, false);

    public TeamView(TeamService teamService, UserRepository userRepository, AuthenticationContext authContext) {
        this.teamService = teamService;

        String username = authContext.getAuthenticatedUser(UserDetails.class)
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new IllegalStateException("Δεν βρέθηκε συνδεδεμένος χρήστης."));

        this.currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Ο χρήστης δεν βρέθηκε στη βάση."));

        add(new H2("Ομάδες Παικτών"));

        // Δημιουργία ομάδας (Issue #10)
        TextField nameField = new TextField("Όνομα ομάδας");
        Button createButton = new Button("Δημιουργία", e -> {
            try {
                teamService.createTeam(nameField.getValue(), currentUser);
                nameField.clear();
                Notification.show("Η ομάδα δημιουργήθηκε!");
                refreshGrids();
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage());
            }
        });
        add(new HorizontalLayout(nameField, createButton));

        // Λίστα: Οι ομάδες μου (Issue #10 - 3ο κριτήριο αποδοχής)
        add(new H3("Οι ομάδες μου"));
        myTeamsGrid.addColumn(team -> team.isCreator(currentUser)
                ? team.getName() + " (δημιουργός)"
                : team.getName()).setHeader("Όνομα");
        myTeamsGrid.addColumn(team -> team.getCreator().getUsername()).setHeader("Δημιουργός");
        myTeamsGrid.addColumn(team -> team.getMembers().size()).setHeader("Μέλη");
        add(myTeamsGrid);

        // Λίστα: Όλες οι ομάδες & συμμετοχή (Issue #11)
        add(new H3("Όλες οι ομάδες"));
        allTeamsGrid.addColumn(Team::getName).setHeader("Όνομα");
        allTeamsGrid.addColumn(team -> team.getCreator().getUsername()).setHeader("Δημιουργός");
        allTeamsGrid.addColumn(team -> team.getMembers().size()).setHeader("Μέλη");
        allTeamsGrid.addComponentColumn(team -> {
            Button joinBtn = new Button("Μπες", e -> {
                try {
                    teamService.joinTeam(team.getId(), currentUser);
                    Notification.show("Μπήκες στην ομάδα!");
                    refreshGrids();
                } catch (IllegalArgumentException ex) {
                    Notification.show(ex.getMessage());
                }
            });
            joinBtn.setEnabled(!team.getMembers().contains(currentUser));
            return joinBtn;
        }).setHeader("Ενέργεια");
        add(allTeamsGrid);

        refreshGrids();
    }

    private void refreshGrids() {
        myTeamsGrid.setItems(teamService.myTeams(currentUser));
        allTeamsGrid.setItems(teamService.listTeams());
    }
}