package com.example.demo.AppUser;

import com.example.demo.AppExpenseMainViewImpl;
import com.example.demo.financeview.AppIncomeMainViewImpl;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.format.DateTimeFormatter;

@Route("user-details")
public class UserDetailsView extends VerticalLayout {

    private final AppUserSerice appUserService;



    public UserDetailsView(AppUserSerice appUserService) {
        this.appUserService = appUserService;



        AppUserModel user = VaadinSession.getCurrent().getAttribute(AppUserModel.class);

        if(user==null){
            UI.getCurrent().navigate(LoginViewImpl.class);
            return;
        }

        getStyle().set("background", "linear-gradient(135deg, #1e3c72, #2a5298)");

        // Avatar
        Avatar avatar = new Avatar(user.getUsername());
        avatar.setColorIndex(1); // Vaadin built-in color theme
        avatar.setImage("https://example.com/profile.png"); // optional photo


        H2 title = new H2("User Profile");
        title.getStyle().set("color", "#2a5298");

        // User details (replace with actual model values)
        Span username = new Span(user.getUsername());
        Span mobile   = new Span(user.getMobileNumber());
        Span email    = new Span(user.getEmailId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = user.getCreatedDate().format(formatter);
        Span created  = new Span(formattedDate);

        username.getStyle().set("font-weight", "bold");
        mobile.getStyle().set("font-weight", "bold");
        email.getStyle().set("font-weight", "bold");
        created.getStyle().set("font-weight", "bold");

        // Action buttons
        Button backBtn = new Button("Back", VaadinIcon.ARROW_LEFT.create());
        backBtn.getStyle().set("background", "linear-gradient(to right, #4facfe, #00f2fe)");
        backBtn.getStyle().set("color", "white");


        Button logoutBtn = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logoutBtn.getStyle().set("background", "linear-gradient(to right, #4facfe, #00f2fe)");
        logoutBtn.getStyle().set("color", "white");


        HorizontalLayout actions = new HorizontalLayout(backBtn, logoutBtn);

        // Card layout
        VerticalLayout card = new VerticalLayout(avatar, title, username, mobile, email, created, actions);
        card.setAlignItems(Alignment.CENTER);
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle().set("box-shadow", "0 8px 20px rgba(0,0,0,0.3)");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("background-color", "white");
        card.setWidth("400px");

        // Center the card
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(card);

        logoutBtn.addClickListener(event -> {
            UI.getCurrent().navigate(LoginViewImpl.class);
        });

        backBtn.addClickListener(event -> {

            Object financeView = VaadinSession.getCurrent().getAttribute("financeValue");
            if(financeView.toString().equalsIgnoreCase("Expense")) {
                UI.getCurrent().navigate(AppExpenseMainViewImpl.class);
            }else{
                UI.getCurrent().navigate(AppIncomeMainViewImpl.class);
            }
        });
    }
}
