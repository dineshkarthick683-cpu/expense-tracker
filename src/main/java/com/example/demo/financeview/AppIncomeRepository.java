package com.example.demo.financeview;

import com.example.demo.AppExpenseMainViewModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppIncomeRepository extends JpaRepository<AppIncomeMainViewModel,Long> {
}
