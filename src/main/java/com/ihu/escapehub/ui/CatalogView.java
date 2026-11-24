package com.ihu.escapehub.ui;

import com.ihu.escapehub.mission.CatalogService;
import com.ihu.escapehub.mission.MissionDefinition;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Κατάλογος αποστολών")
public class CatalogView extends VerticalLayout {

    // Προσωρινά: η ομάδα του χρήστη θα έρθει από το Μέλος 2.
    private static final Long TEAM_ID = 1L;

    private final CatalogService catalogService;

    public CatalogView(CatalogService catalogService) {
        this.catalogService = catalogService;

        Grid<MissionDefinition> grid = new Grid<>(MissionDefinition.class, false);
        grid.addColumn(MissionDefinition::title).setHeader("Τίτλος").setAutoWidth(true);
        grid.addColumn(MissionDefinition::description).setHeader("Περιγραφή").setFlexGrow(1);
        grid.addComponentColumn(mission -> {
            Button start = new Button("Ξεκίνα", e -> startMission(mission));
            start.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return start;
        }).setHeader("");
        grid.setItems(catalogService.listMissions());

        add(new H2("Κατάλογος αποστολών"), grid);
    }

    private void startMission(MissionDefinition mission) {
        try {
            catalogService.start(TEAM_ID, mission.id());
            Notification.show("Η αποστολή «" + mission.title() + "» ξεκίνησε!");
        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage());
        }
    }
}