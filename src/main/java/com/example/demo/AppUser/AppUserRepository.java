package com.example.demo.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUserModel,Long> {

    AppUserModel findByUsername(String username);

    AppUserModel findByEmailId(String emailId);

    AppUserModel findByMobileNumber(String mobileNumber);
}
