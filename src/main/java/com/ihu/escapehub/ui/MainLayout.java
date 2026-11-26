package com.ihu.escapehub.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@PermitAll
public class MainLayout extends AppLayout {

    public MainLayout(AuthenticationContext authenticationContext) {
        H1 title = new H1("EscapeHub");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        Button logout = new Button("Αποσύνδεση", e -> authenticationContext.logout());

        addToNavbar(new DrawerToggle(), title, logout);

        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Κατάλογος", "", VaadinIcon.BOOK.create()));
        nav.addItem(new SideNavItem("Ομάδες", "teams", VaadinIcon.GROUP.create()));
        nav.addItem(new SideNavItem("Ημερολόγιο", "evidence", VaadinIcon.CALENDAR.create()));

        addToDrawer(nav);
    }
}