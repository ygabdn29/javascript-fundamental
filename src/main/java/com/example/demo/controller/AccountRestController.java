package com.example.demo.controller;

import java.util.UUID;

import org.hibernate.id.GUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.handler.Utils;
import com.example.demo.model.Employee;
import com.example.demo.model.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.UserService;

import ch.qos.logback.classic.pattern.Util;

@RestController
@RequestMapping("api/account")
public class AccountRestController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @GetMapping("register/verification")
    public ResponseEntity<Object> sendEmailVerification(@RequestBody User user) {

        User verifyUser = userService.authenticate(user.getUsername(), user.getPassword());

        // Generate Guid
        if (verifyUser != null) {
            UUID guid = UUID.randomUUID();
            String guidString = guid.toString();
            //insert the guidString to db
            verifyUser.setGuid(guidString);
            userService.save(verifyUser);
            String linkMailString = "http://localhost:8080/api/account/verification/" + guidString;

            Employee employee = verifyUser.getEmployee();

            if (employee != null && employee.getEmail() != null) {
                String text = "This is your verification email, please click on it:\n"+linkMailString;
                emailService.sendEmail(employee.getEmail(), "Account Verification", text);
                return Utils.generateResponseEntity(HttpStatus.OK, "Email has been sent");
            }
        }

        return Utils.generateResponseEntity(HttpStatus.BAD_REQUEST, "Email failed to be sent");

    }

    @GetMapping("register/verification/{guid}")
    public ResponseEntity<Object> verified(@PathVariable String guid){
        User verifiedUser = userService.verifyUser(guid);
        if(verifiedUser != null){
            userService.save(verifiedUser);
            return Utils.generateResponseEntity(HttpStatus.OK, "Account has been verified", verifiedUser);
        }
        return Utils.generateResponseEntity(HttpStatus.BAD_REQUEST, "Verification is failed");

    }

}
