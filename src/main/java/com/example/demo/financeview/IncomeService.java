package com.example.demo.financeview;
import com.example.demo.AppExpenseMainViewModel;
import com.example.demo.AppExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncomeService {

    @Autowired
    private AppIncomeRepository repository;

    public void save(AppIncomeMainViewModel model) {
        repository.save(model);
    }

    public List<AppIncomeMainViewModel> getAllIncome() {
        return repository.findAll();
    }

    public List<AppIncomeMainViewModel> searchIncome(
            String category,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return repository.findAll()
                .stream()
                .filter(e -> category == null ||
                        category.equalsIgnoreCase(
                                e.getCategory()))
                .filter(e -> startDate == null ||
                        !e.getIncomeDate()
                                .isBefore(startDate))
                .filter(e -> endDate == null ||
                        !e.getIncomeDate()
                                .isAfter(endDate))
                .toList();


    }

    public void deleteIncome(Long id) {

        repository.deleteById(id);
    }
}