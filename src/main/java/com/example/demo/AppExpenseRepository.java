package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppExpenseRepository extends JpaRepository<AppExpenseMainViewModel,Long> {
}
