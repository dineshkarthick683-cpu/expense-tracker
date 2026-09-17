package com.example.demo.AppUser;

import com.vaadin.flow.component.UI;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name ="APP_USER")
public class AppUserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "USERNAME", nullable = true, unique = true, length = 100)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "MOBILE_NUMBER", nullable = true, length = 15)
    private String mobileNumber;

    @Column(name = "EMAIL_ID", nullable = false, unique = true, length = 150)
    private String emailId;

    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;

    // --- Getters & Setters ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername()
    {
        if(username==null){
            UI.getCurrent().navigate(LoginViewImpl.class);
            return null;
        }
        return username;
    }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

}
