package com.example.demo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.EmployeeService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import com.example.demo.service.EmailService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.handler.Utils;
import com.example.demo.model.Employee;
import com.example.demo.model.RegistrationDTO;
import com.example.demo.model.Role;
import com.example.demo.model.User;

@RestController
@RequestMapping("api/account")
public class AccountRestController {
  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private UserService userService;

  @Autowired
  private RoleService roleService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private EmailService emailService;

  @PostMapping("/register")
  public ResponseEntity<Object> register(@RequestBody RegistrationDTO registrationDTO) {
    try {
      Employee employee = new Employee(null, registrationDTO.getName(), registrationDTO.getEmail(), registrationDTO.getBirthDate(), registrationDTO.getAddress(), registrationDTO.getPhone());
      employeeService.save(employee);

      Role defaultRole = roleService.getRoleWithLowestLevel();
      String guid = UUID.randomUUID().toString();
      User user = new User(registrationDTO.getPassword(), registrationDTO.getUsername(), defaultRole, false, null, employee);
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      user.setEmployee(employee);
      user.setGuid(guid);
      userService.save(user);

      String subject = "Email Verification";
      String confirmationUrl = "http://localhost:8080/api/account/verify/" + user.getGuid();
      String message = "Click the link to verify your email: \n" + confirmationUrl;
      emailService.sendEmail(employee.getEmail(), subject, message);

      return Utils.generaResponseEntity(HttpStatus.OK,
          "Registration Successful. A verification email has been sent to your email address.");
    } catch (Exception e) {
      return Utils.generaResponseEntity(HttpStatus.OK,
          "Registration Failed: " + e.getMessage());
    }
  }

  @PostMapping("/verify/{guid}")
  public ResponseEntity<Object> verifyEmail(@PathVariable String guid) {
    User user = userService.verifyUser(guid);
    if (user != null) {
      user.setIsVerified(true);
      user.setGuid(null);
      userService.save(user);
      return Utils.generaResponseEntity(HttpStatus.OK, "Verification Account successfully");
    }
    return Utils.generaResponseEntity(HttpStatus.OK, "Verification Failed");
  }
}
