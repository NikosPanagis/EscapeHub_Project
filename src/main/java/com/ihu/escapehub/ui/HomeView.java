package com.ihu.escapehub.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("EscapeHub")
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(new H1("EscapeHub"), new Paragraph("Project skeleton is running"));
    }
}