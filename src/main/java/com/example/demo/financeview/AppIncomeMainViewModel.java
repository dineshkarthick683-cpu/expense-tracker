package com.example.demo.financeview;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name ="income_main")
public class AppIncomeMainViewModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long income_id;

    @Column(name = "category")
    private String category;

    public Long getId() {
        return income_id;
    }

    public void setId(Long id) {
        this.income_id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(LocalDateTime incomeDate) {
        this.incomeDate = incomeDate;
    }

    public LocalDateTime getCreated_time() {
        return created_time;
    }

    public void setCreated_time(LocalDateTime created_time) {
        this.created_time = created_time;
    }

    @Column(name = "username")
    private String username;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "income")
    private Double amount;

    @Column(name = "income_date")
    private LocalDateTime incomeDate;

    @Column(name = "created_time")
    private LocalDateTime created_time;

    @Column(name = "location")
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}