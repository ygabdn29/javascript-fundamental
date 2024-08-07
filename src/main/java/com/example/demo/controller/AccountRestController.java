package com.example.demo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.handler.utils;
import com.example.demo.model.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;




@RestController
@RequestMapping("api/account")
public class AccountRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @GetMapping("password") //kirim email
    public ResponseEntity<Object> sendEmailChangePassword(@RequestBody User user) { 
        User validUser = userService.authenticate(user.getUsername(), user.getPassword());
        if(validUser != null){
            String guid = UUID.randomUUID().toString();
            validUser.setGuid(guid); // set user lokal
            userService.save(validUser); //update ke db
            String linkEmailGuid = "http://localhost:8080/api/account/password/change/" + guid;
            emailService.sendEmail("reza.mahesa.azandi@gmail.com", "Change password", "Please click the link below to change your password\n" + linkEmailGuid);
            return utils.generateResponseEntity(HttpStatus.OK, "We've sent you an email");
        }
        else{
            return utils.generateResponseEntity(HttpStatus.OK, "Wrong username or password");
        }
    }

    @PostMapping("password/change/{guid}")
    public ResponseEntity<Object> changePasswordGuid(@PathVariable String guid, @RequestHeader String recentPass, 
                                                    @RequestHeader String newPass, @RequestHeader String confirmPass){
        User user = userService.findByStringGuid(guid);
        User validatedUser = userService.validatePassword(user, recentPass, newPass, confirmPass);
        if(validatedUser == null){
            user.setGuid(null);
            userService.save(user);
            return utils.generateResponseEntity(HttpStatus.OK, "Invalid Password or Unmatch Password");
        }
        validatedUser.setPassword(passwordEncoder.encode(newPass));
        validatedUser.setGuid(null);
        userService.save(validatedUser);
        return utils.generateResponseEntity(HttpStatus.OK, "Password successfully has been changed");
    }
    
}
