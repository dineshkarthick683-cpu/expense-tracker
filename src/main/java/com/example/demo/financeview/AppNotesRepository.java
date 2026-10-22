package com.example.demo.financeview;

import com.example.demo.AppExpenseMainViewModel;
import com.example.demo.AppUser.AppUserNotesModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppNotesRepository extends JpaRepository<AppUserNotesModel,Integer> {
}
