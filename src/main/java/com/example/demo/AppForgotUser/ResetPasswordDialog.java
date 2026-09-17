package com.example.demo.AppForgotUser;

import com.example.demo.AppUser.AppUserModel;
import com.example.demo.AppUser.AppUserSerice;
import com.example.demo.AppUser.LoginViewImpl;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;

@Route("reset-password")
public class ResetPasswordDialog extends Dialog {

    private final AppUserModel appUserModel;

    private final AppUserSerice appUserSerice;

    public ResetPasswordDialog(AppUserModel appUserModel, AppUserSerice appUserSerice) {
        this.appUserModel = appUserModel;
        this.appUserSerice = appUserSerice;


        H2 title = new H2("Reset Password");
        title.getStyle().set("color", "#2a5298");

        TextField usernameField = new TextField("Username");
        usernameField.setPrefixComponent(VaadinIcon.USER.create());

        PasswordField newPasswordField = new PasswordField("New Password");
        newPasswordField.setPrefixComponent(VaadinIcon.LOCK.create());

        PasswordField confirmPasswordField = new PasswordField("Confirm New Password");
        confirmPasswordField.setPrefixComponent(VaadinIcon.LOCK.create());

        Button resetBtn = new Button("Reset Password", VaadinIcon.REFRESH.create());
        resetBtn.getStyle().set("background", "linear-gradient(to right, #4facfe, #00f2fe)");
        resetBtn.getStyle().set("color", "white");

        Button backBtn = new Button("Back to Login", VaadinIcon.ARROW_LEFT.create());
        backBtn.getStyle().set("background", "linear-gradient(to right, #4facfe, #00f2fe)");
        backBtn.getStyle().set("color", "white");
        backBtn.addClickListener(e -> {
            this.close();
            UI.getCurrent().navigate(LoginViewImpl.class);
        });

        //HorizontalLayout actions = new HorizontalLayout(resetBtn, backBtn);

        resetBtn.setWidthFull();
        backBtn.setWidthFull();

        VerticalLayout card = new VerticalLayout(title, usernameField, newPasswordField, confirmPasswordField, resetBtn,backBtn);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.getStyle().set("box-shadow", "0 8px 20px rgba(0,0,0,0.3)");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("background-color", "white");
        //card.setWidth("400px");
        card.setWidthFull();

        add(card);

        resetBtn.addClickListener(event -> {
            String username = usernameField.getValue();
            String newPass = newPasswordField.getValue();
            String confirmPass = confirmPasswordField.getValue();

            if (!newPass.equals(confirmPass)) {
                Notification.show("Passwords do not match!");
                return;
            }


            if (appUserModel != null) {
                appUserModel.setPassword(newPass); // ⚠️ Hash in real apps
                appUserModel.setCreatedDate(LocalDateTime.now());
                appUserSerice.save(appUserModel);
                Notification.show("Password reset successful!");
                this.close();
                UI.getCurrent().navigate(LoginViewImpl.class);
            } else {
                Notification.show("User not found!");
            }
        });
    }
}

