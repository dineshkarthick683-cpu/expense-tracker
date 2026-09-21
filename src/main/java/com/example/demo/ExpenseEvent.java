package com.example.demo;

public class ExpenseEvent {

    private Long expenseId;
    private String category;
    private Double amount;

    public ExpenseEvent() {
    }

    public ExpenseEvent(Long expenseId,
                        String category,
                        Double amount) {
        this.expenseId = expenseId;
        this.category = category;
        this.amount = amount;
    }

    // getters setters

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}