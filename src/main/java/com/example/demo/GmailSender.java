package com.example.demo;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class GmailSender {

    private final JavaMailSender mailSender;

    public GmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to,
                          String subject,
                          String text) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}

/* MSG91
Step 1: Create Auth Key Using MSG91
Step 2: Create Template ID
Step 3: Create Sender ID like message come from where AMZON,ZEPTO,EXP_APP,Store MSG91 configuration in application.properties
Step 4: Build URL
                String urlStr = "https://api.msg91.com/api/sendhttp.php?authkey=" + authKey
                        + "&mobiles=" + mobileNumber
                        + "&message=" + URLEncoder.encode(message, "UTF-8")
                       + "&sender=" + senderId
                        + "&route=" + route;
Step 5: Spring Boot application sends an HTTP request containing the Auth Key, mobile number, and message/template details to MSG91.
Step 6: MSG91 processes the request and delivers the SMS to the recipient's mobile number.

Note: In this case, for the local application,
we are unable to create a Template or Sender ID because we need to register a PE ID and complete the DLT registration first.

Who are you? → PE ID
Can you send SMS? → DLT Approval
DLT = Government-approved SMS registration system , DLT = Permission to send SMS
PE = Principal Entity ID, It is like your company/business ID
*/

/* GMAIL OTP
Step 1: Use the JavaMailSender API to create the email body, set the subject, and set the email content or text.
Step 2: Configure SMTP in application.properties
         spring.mail.host=smtp.gmail.com
        spring.mail.port=587
        spring.mail.username=yourgmail@gmail.com
        spring.mail.password=your_app_password
        spring.mail.properties.mail.smtp.auth=true
        spring.mail.properties.mail.smtp.starttls.enable=true
Step 3: Enable Google 2-Step Verification
Step 4: Generate App Password
Step 5: set in application.property

Backend Step 1: Click Listener Executes
Backend Step 2: Generate OTP
Backend Step 3: Store OTP(VaadinSession.getCurrent().setAttribute("otp", "654321");)
Backend Step 4: Create Email Object
Backend Step 5: JavaMailSender Uses SMTP
Spring reads:Application Property
Backend Step 6: SMTP Authentication

*/