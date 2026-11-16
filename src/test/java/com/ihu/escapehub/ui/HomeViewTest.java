package com.ihu.escapehub.ui;

import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HomeViewTest {

    @Test
    void showsApplicationNameAsHeading() {
        HomeView view = new HomeView();

        List<String> headings = view.getChildren()
                .filter(H1.class::isInstance)
                .map(component -> ((H1) component).getText())
                .toList();

        assertEquals(List.of("EscapeHub"), headings);
    }
}
