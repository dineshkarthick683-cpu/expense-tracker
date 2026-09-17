package com.example.demo;
import com.example.demo.AppExpenseMainViewModel;
import com.example.demo.AppExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private AppExpenseRepository repository;

    public void save(AppExpenseMainViewModel model) {
        repository.save(model);
    }

    public List<AppExpenseMainViewModel> getAllExpenses() {
        return repository.findAll();
    }

    public List<AppExpenseMainViewModel> searchExpenses(
            String category,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return repository.findAll()
                .stream()
                .filter(e -> category == null ||
                        category.equalsIgnoreCase(
                                e.getCategory()))
                .filter(e -> startDate == null ||
                        !e.getExpenseDate()
                                .isBefore(startDate))
                .filter(e -> endDate == null ||
                        !e.getExpenseDate()
                                .isAfter(endDate))
                .toList();


    }

    public void deleteExpense(Long id) {
        repository.deleteById(id);
    }
}