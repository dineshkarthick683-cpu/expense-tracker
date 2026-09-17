package com.example.demo;

public class Transaction {

    private String category;
    private Double amount;

    public Transaction(String category, Double amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public Double getAmount() {
        return amount;
    }
}