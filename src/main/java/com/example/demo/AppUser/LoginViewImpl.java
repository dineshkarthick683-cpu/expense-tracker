package com.example.demo.AppUser;

import com.example.demo.AppExpenseMainViewImpl;
import com.example.demo.AppForgotUser.ResetPasswordDialog;
import com.example.demo.GmailSender;
import com.example.demo.financeview.FinanceView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Collections;

@Route("")  // default route → loads first
public class LoginViewImpl extends VerticalLayout {


    private AppUserSerice appUserService;

    private final GmailSender mailSenderService;

    private final PasswordEncoder passwordEncoder;


    private final JwtService jwtService;


    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();




    public LoginViewImpl(AppUserSerice appUserService, GmailSender mailSenderService,PasswordEncoder passwordEncoder,JwtService jwtService) {
        // Title + subtitle

        this.appUserService = appUserService;

        this.mailSenderService = mailSenderService;

        this.passwordEncoder = passwordEncoder;

        this.jwtService = jwtService;

        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oauthUser) {

            AppUserModel user = new AppUserModel();

            user.setUsername(oauthUser.getAttribute("name"));

            user.setEmailId(oauthUser.getAttribute("email"));

            user.setCreatedDate(LocalDateTime.now());

            VaadinSession.getCurrent().setAttribute(AppUserModel.class, user);
        }

        getStyle().set("background", "linear-gradient(135deg, #1e3c72, #2a5298)");

        // Forgot Password link
        Anchor forgotPasswordLink = new Anchor("#", "Forgot Password?");
        forgotPasswordLink.getStyle().set("color", "#2a5298");
        forgotPasswordLink.getStyle().set("text-decoration", "underline");

        // Forgot Password link
        Anchor registerLink = new Anchor("#", "Register");
        registerLink.getStyle().set("color", "#2a5298");
        registerLink.getStyle().set("text-decoration", "underline");


//          /oauth2/authorization/google → this is the endpoint Spring Security exposes automatically when you configure Google OAuth in application.yml.
//
//          When clicked, the button redirects the browser to Google’s login page.
//
//          After login, Google sends the user back to your redirect URI (http://localhost:8080/login/oauth2/code/google).
//
//          Spring Security validates the token and then sends the user to /Main (your Vaadin view).


        // Link to Spring Security's OAuth2 authorization endpoint


        // Link to Spring Security's OAuth2 authorization endpoint
        Button googleLoginButton = new Button("Login with Google", e -> {
            // Redirect the browser to Spring Security's OAuth2 authorization endpoint
            UI.getCurrent().getPage().setLocation("/oauth2/authorization/google");
        });

        // Style it like a primary button
        googleLoginButton.getElement().getStyle()
                .set("color", "white")
                .set("background-color", "#1976d2")
                .set("padding", "10px 20px")
                .set("border-radius", "5px")
                .set("font-weight", "600");



        // User icon
        Icon userIcon = VaadinIcon.USER.create();
        userIcon.setSize("64px");
        userIcon.getStyle().set("color", "#2a5298");

        H2 title = new H2("Welcome Back!");
        title.getStyle().set("color", "black");
        Span subtitle = new Span("Please login to continue");
        subtitle.getStyle().set("color", "black");

        TextField usernameField = new TextField("Username");
        usernameField.setPrefixComponent(VaadinIcon.USER.create());

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());

        Button loginBtn = new Button("Login", VaadinIcon.SIGN_IN.create());
        loginBtn.getStyle().set("background", "linear-gradient(to right, #4facfe, #00f2fe)");
        loginBtn.getStyle().set("color", "white");

        Button resetBtn = new Button("Reset", VaadinIcon.REFRESH.create());
        resetBtn.getStyle().set("background", "linear-gradient(to right, #4facfe, #00f2fe)");
        resetBtn.getStyle().set("color", "white");

        HorizontalLayout btnLayout = new HorizontalLayout(loginBtn, resetBtn);
        HorizontalLayout btnLinkt = new HorizontalLayout(forgotPasswordLink, registerLink);
        HorizontalLayout btnGoogleLayout = new HorizontalLayout(googleLoginButton);


        VerticalLayout card = new VerticalLayout(userIcon, title, subtitle,
                usernameField, passwordField, btnLayout,btnLinkt,btnGoogleLayout);
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle().set("box-shadow", "0 8px 20px rgba(0,0,0,0.3)");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("background-color", "white");
        card.setWidth("400px");

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(card);

        loginBtn.addClickListener(event -> {

            String username = usernameField.getValue();
            String password = passwordField.getValue();
            // Check if user exists
            AppUserModel existingUser = appUserService.findByUserName(username);
            if (existingUser != null) {
                // User exists → check password



                if (password.equals(existingUser.getPassword())) {
                //if (passwordEncoder.matches(password, existingUser.getPassword())) {

                    String token = jwtService.generateToken(username,"admin");
                    VaadinSession.getCurrent().setAttribute("jwt", token); // store token

                    if (jwtService.validateToken(token, username) && !jwtService.isTokenExpired(token)) {

                        Notification.show("Login successful. Welcome " + username);
                        VaadinSession.getCurrent().setAttribute(AppUserModel.class, existingUser);

                        UI.getCurrent().navigate(FinanceView.class);
                        //UI.getCurrent().navigate(AppExpenseMainViewImpl.class);
                    }

                } else {
                    Notification.show("Invalid password for user: " + username);
                }
            }else{
                Notification.show("Invalid user: " + username);
            }
        });




        forgotPasswordLink.getElement().addEventListener("click", e -> {
            AppUserModel userModelObj = appUserService.findByUserName(usernameField.getValue());
            ResetPasswordDialog resetDialog = new ResetPasswordDialog(userModelObj,appUserService);
            resetDialog.open();
        });



        registerLink.getElement().addEventListener("click", e -> {

            AppUserRegisterDialog resetDialog = new AppUserRegisterDialog(appUserService,mailSenderService,passwordEncoder);
            resetDialog.open();
        });
        // Reset button action → clear fields
        resetBtn.addClickListener(event -> {
            usernameField.clear();
            passwordField.clear();
            //delete();

        });
    }


//Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8))


    public void delete() {
        appUserService.delete();
    }

}
