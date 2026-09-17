package com.example.demo.AppUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserSerice {

    @Autowired
    AppUserRepository userRepository;

    public List<AppUserModel> findAll(){

        return userRepository.findAll();

    }

    public AppUserModel save(AppUserModel appUserModel){

        return userRepository.save(appUserModel);

    }

    public AppUserModel findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public AppUserModel findByEmailId(String emailId) {
        return userRepository.findByEmailId(emailId);
    }

    public AppUserModel findByPhoneNumber(String mobileNumber) {
        return userRepository.findByMobileNumber(mobileNumber);
    }

}
