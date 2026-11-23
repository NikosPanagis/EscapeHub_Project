package com.ihu.escapehub.ui;

import com.ihu.escapehub.user.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "register", autoLayout = false)
@PageTitle("Εγγραφή | EscapeHub")
@AnonymousAllowed
public class RegisterView extends Main {

    private final UserService userService;
    private final TextField usernameField = new TextField("Username");
    private final PasswordField passwordField = new PasswordField("Password");

    public RegisterView(UserService userService) {
        this.userService = userService;

        passwordField.setHelperText("Τουλάχιστον 6 χαρακτήρες");

        Button registerButton = new Button("Εγγραφή", event -> register());
        registerButton.addClickShortcut(Key.ENTER);

        VerticalLayout layout = new VerticalLayout(
                new H2("Δημιουργία λογαριασμού"),
                usernameField,
                passwordField,
                registerButton,
                new Anchor("login", "Έχεις ήδη λογαριασμό; Σύνδεση"));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(layout);
    }

    private void register() {
        try {
            userService.register(usernameField.getValue(), passwordField.getValue());
            Notification.show("Επιτυχής εγγραφή. Μπορείς τώρα να συνδεθείς.");
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        } catch (IllegalArgumentException e) {
            passwordField.clear();
            Notification.show(e.getMessage());
        }
    }
}