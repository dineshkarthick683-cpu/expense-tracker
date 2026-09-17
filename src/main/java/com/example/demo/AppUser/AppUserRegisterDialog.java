package com.example.demo.AppUser;
import com.example.demo.GmailSender;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class AppUserRegisterDialog extends Dialog {

    private final AppUserSerice appUserService;

    private final GmailSender mailSenderService;

    private final PasswordEncoder passwordEncoder;

    public AppUserRegisterDialog(AppUserSerice appUserService, GmailSender mailSenderService, PasswordEncoder passwordEncoder) {

        this.appUserService = appUserService;

        this.mailSenderService = mailSenderService;

        this.passwordEncoder = passwordEncoder;



        // User icon
        Icon userIcon = VaadinIcon.USER.create();
        userIcon.setSize("64px");
        userIcon.getStyle().set("color", "#2a5298");

        H2 title = new H2("Welcome Back!");
        Span subtitle = new Span("Please register to login");

        TextField usernameField = new TextField("Username");
        usernameField.setPrefixComponent(VaadinIcon.USER.create());
        usernameField.setWidthFull();

        TextField phoneField = new TextField("Phone Number");
        phoneField.setPrefixComponent(VaadinIcon.PHONE.create());
        phoneField.setWidthFull();

        TextField emailField = new TextField("Email ID");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setWidthFull();

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setWidthFull();

        Button signUpBtn = new Button("Sign up", VaadinIcon.SIGN_IN.create());
        signUpBtn.getStyle().set("background", "linear-gradient(to right, #4facfe, #00f2fe)");
        signUpBtn.getStyle().set("color", "white");

        Button resetBtn = new Button("Reset", VaadinIcon.REFRESH.create());

        Button backBtn = new Button("Back", VaadinIcon.ARROW_LEFT.create());

        TextField otpField = new TextField("Enter OTP");
        otpField.setPlaceholder("6-digit code");
        otpField.setWidthFull();
        //otpField.setVisible(false); // initially hidden

        // Create button with icon
        Button sendOtpBtn = new Button("Send Gmail OTP", VaadinIcon.PAPERPLANE.create());

        // Style the button
        sendOtpBtn.getStyle()
                .set("background", "linear-gradient(to right, #4facfe, #00f2fe)")
                .set("color", "white")
                .set("font-weight", "600")
                //.set("width", "52%")
                .set("justify-content", "flex-start"); // ✅ text + icon aligned left

        sendOtpBtn.setWidthFull();
        //confirmOtpBtn.setVisible(false); // show only after OTP is sent


        HorizontalLayout btnLayout = new HorizontalLayout(signUpBtn, resetBtn,backBtn);

        HorizontalLayout btnBackLayout = new HorizontalLayout(backBtn);

        VerticalLayout card = new VerticalLayout(userIcon, title, subtitle,
                usernameField, phoneField, emailField, passwordField,sendOtpBtn, otpField, btnLayout,btnBackLayout);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.getStyle().set("box-shadow", "0 8px 20px rgba(0,0,0,0.3)");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("background-color", "white");
        //card.setWidth("400px");
        //card.setHeight("800px"); // or any value you want
        card.setWidthFull();
        card.setHeight(null);

        add(card);

        // Sign up logic
        signUpBtn.addClickListener(event -> {
            String email = emailField.getValue();
            String username = usernameField.getValue();
            String password = passwordField.getValue();
            String mobile = phoneField.getValue();

            if (username == null || username.trim().isEmpty()) {
                Notification.show("Please enter a username");
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                Notification.show("Please enter a password");
                return;
            }

            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                Notification.show("Please enter a valid email address");
                return;
            }
            if (mobile == null || !mobile.matches("\\d{10}")) {
                Notification.show("Phone number must be exactly 10 digits");
                return;
            }


            if (appUserService.findByUserName(username) != null) {
                Notification.show("Duplicate User Id: " + username);
                return;
            }
            if (appUserService.findByEmailId(email) != null) {
                Notification.show("Duplicate Email Id for user: " + username);
                return;
            }
            if (appUserService.findByPhoneNumber(mobile) != null) {
                Notification.show("Duplicate Phone Number for user: " + username);
                return;
            }


            String storedOtp = (String) VaadinSession
                    .getCurrent()
                    .getAttribute("otp");

            String enteredOtp = otpField.getValue();

            if (storedOtp == null ||
                    !storedOtp.equals(enteredOtp)) {

                Notification.show("OTP Verification failed!");
                return;
            }

            AppUserModel newUser = new AppUserModel();
            newUser.setUsername(username);
            //newUser.setPassword(password); // ⚠️ Hash in real apps
            newUser.setEmailId(email);
            newUser.setMobileNumber(mobile);
            newUser.setCreatedDate(LocalDateTime.now());
            newUser.setPassword(
                    passwordEncoder.encode(passwordField.getValue())
            );
            appUserService.save(newUser);

            VaadinSession.getCurrent().setAttribute(AppUserModel.class, newUser);

            Notification.show("New user created. Welcome " + username);
            this.close();
            UI.getCurrent().navigate(LoginViewImpl.class);
        });

        resetBtn.addClickListener(event -> {
            usernameField.clear();
            passwordField.clear();
            phoneField.clear();
            emailField.clear();
            sendOtpBtn.setText("Send OTP");
        });


//        sendOtpBtn.addClickListener(event -> {
//            try {
//                // Generate random 6-digit OTP
//                int otp = (int)(Math.random() * 900000) + 100000;
//
//                // Save OTP in session for later verification
//                VaadinSession.getCurrent().setAttribute("otp", otp);
//
//                String authKey = "YOUR_MSG91_AUTH_KEY";
//                String mobileNumber = "+91" + phoneField.getValue(); // user input
//                String message = "Your OTP is " + otp;
//                String senderId = "MSGIND"; // must be approved in MSG91
//                String route = "4"; // transactional route
//
//                // Build URL
//                String urlStr = "https://api.msg91.com/api/sendhttp.php?authkey=" + authKey
//                        + "&mobiles=" + mobileNumber
//                        + "&message=" + URLEncoder.encode(message, "UTF-8")
//                        + "&sender=" + senderId
//                        + "&route=" + route;
//
//                // Open connection
//                URL url = new URL(urlStr);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//
//                int responseCode = conn.getResponseCode();
//                if (responseCode == 200) {
//                    Notification.show("OTP sent successfully to " + mobileNumber);
//                } else {
//                    Notification.show("Failed to send OTP. Response code: " + responseCode);
//                }
//
//                conn.disconnect(); // ✅ close connection
//
//            } catch (Exception e) {
//                Notification.show("Error sending OTP: " + e.getMessage());
//            }
//        });

        sendOtpBtn.addClickListener(event -> {
            try {

                int otp = (int) (Math.random() * 900000) + 100000;

                VaadinSession.getCurrent()
                        .setAttribute("otp", String.valueOf(otp));

                mailSenderService.sendEmail(
                        emailField.getValue(),
                        "OTP Verification",
                        "Dear User,\n\n"
                                + "Your OTP is: " + otp + "\n\n"
                                + "This OTP is valid for 5 minutes.\n"
                                + "Please do not share it with anyone.");

                Notification.show(
                        "OTP sent successfully to "
                                + emailField.getValue());

            } catch (Exception e) {
                e.printStackTrace();

                Notification.show(
                        "Failed to send OTP: "
                                + e.getMessage());
            }
        });






        backBtn.addClickListener(event -> {
        this.close();
        UI.getCurrent().navigate(LoginViewImpl.class);
        });
    }
}
