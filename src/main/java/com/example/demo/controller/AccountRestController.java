package com.example.demo.controller;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.handler.Utils;

import com.example.demo.model.Employee;
import com.example.demo.model.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.EmployeeService;

import com.example.demo.service.UserService;

@RestController
@RequestMapping("api/account")
public class AccountRestController {

  @Autowired
  private UserService userService;
  @Autowired
  private EmailService emailService;
   @Autowired
   private EmployeeService employeeService;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping("login")
  public ResponseEntity<Object> login(@RequestBody User userLogin ){
    User auntheticatedUser = userService.authenticate(userLogin.getUsername(), userLogin.getPassword());

    if(auntheticatedUser != null && !auntheticatedUser.getIsVerified()){
      return Utils.generaResponseEntity(HttpStatus.OK, "Not Verified Yet!");
    }

    try{ 
      org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
      auntheticatedUser.getId().toString(),
      "",
      getAuthorities(auntheticatedUser.getRole().getName())
    );

    PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(
      user,
      "", 
      user.getAuthorities()
      );
    
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

  

    return Utils.generaResponseEntity(HttpStatus.OK, "Login Success!");
    } catch(Exception e){
      return Utils.generaResponseEntity(HttpStatus.OK, "Credentials Doesn't Match Any Records!");
    }
  }

  private static Collection<? extends GrantedAuthority> getAuthorities(String role){

    final List<SimpleGrantedAuthority> authorities = new LinkedList<>();
    authorities.add(new SimpleGrantedAuthority(role));
    return authorities;
  }

  @GetMapping("{id}/password")
  public ResponseEntity<Object> sendMailChangePassword(@PathVariable Integer id,  @RequestBody User user){
    User validUser = userService.authenticate(user.getUsername(), user.getPassword());
    // generate guid + setguid
    // String guidString = UUID.randomUUID().toString();
    // save user
    String linkMail = "http://localhost:8080/api/account" + id + "/password/change";
    if(validUser != null){
      emailService.sendMail("yogabudiman07@gmail.com", "Change Password", "Please click the link below to change your password\n" + linkMail);
      return Utils.generaResponseEntity(HttpStatus.OK, "We've sent you an email");
    } else {
      return Utils.generaResponseEntity(HttpStatus.OK, "Wrong username or password");
    }
  }

  @PostMapping("{id}/password/change")
  public ResponseEntity<Object> changePassword(@PathVariable Integer id, @RequestHeader String recentPass, @RequestHeader String newPass, @RequestHeader String confirmPass){
    User user = userService.get(id);
    User validatedUser = userService.validatePassword(user, recentPass, newPass, confirmPass);

    if(validatedUser == null){
      return Utils.generaResponseEntity(HttpStatus.OK, "Invalid password or unmatch password");
    }
    // setguid null
    // save user
    validatedUser.setPassword(passwordEncoder.encode(newPass));
    userService.save(validatedUser);
    return Utils.generaResponseEntity(HttpStatus.OK, "Password successfully has been changed");
  }

    @PostMapping("findEmail")
    public ResponseEntity<String> processForgotPassword(@RequestBody Employee employee) {
        // Extract email from the request body
        String email = employee.getEmail();
        
        // Validate email parameter
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                                 .body("Email parameter is missing or empty");
        }

        // Find employee by email
        Employee foundEmployee = employeeService.findByEmail(email);

        // Check if the employee is found and return the ID
        if (foundEmployee != null) {
            return ResponseEntity.ok("Employee ID: " + foundEmployee.getId() + " Email: " + foundEmployee.getEmail());
            
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Employee not found");
        }


    }

    // send Email
    @GetMapping("{id}/password")
    public ResponseEntity<Object> sendEmailChangePassword(@PathVariable Integer id, @RequestBody User user){
        User user2 = userService.authenticate(user.getUsername(), user.getPassword());
        String linkEmail  = "http://localhost:8080/api/account/" + id + "/password/change";

        if (user2 != null) {
            emailService.sendEmail("fanstein48@gmail.com", "change password", "plis click the link\n" + linkEmail);
            return Utils.generateResponseEntity(HttpStatus.OK, "Email Berhasil DIkirim");
        }else{
            return Utils.generateResponseEntity(HttpStatus.OK, "Email Gagal Dikirim");
        }
    }

    @PostMapping("validate-password")
    public ResponseEntity<Object> validatePassword(@RequestBody String password) {
        if (userService.isValidPassword(password)) {
            return Utils.generateResponseEntity(HttpStatus.OK, "Password Valid");
        } else {
            return Utils.generateResponseEntity(HttpStatus.OK, "Password tidak valid. Password harus memiliki minimal 8 karakter, mengandung huruf besar, huruf kecil, dan angka.");
        }
    }

    @PostMapping("reset-password")
    public ResponseEntity<Object> processResetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            return Utils.generateResponseEntity(HttpStatus.BAD_REQUEST, "Email and password are required.");
        }

        Employee foundEmployee = employeeService.findByEmail(email);
        if (foundEmployee == null) {
            return Utils.generateResponseEntity(HttpStatus.BAD_REQUEST, "Invalid email");
        }

        Integer employeeId = foundEmployee.getId();
        User user = userService.get(employeeId);
        if (user == null) {
            return Utils.generateResponseEntity(HttpStatus.NOT_FOUND, "User not found.");
        }

        user.setPassword(passwordEncoder.encode(password));
        userService.save(user);

        return Utils.generateResponseEntity(HttpStatus.OK, "Password has been reset successfully.");
    }
    

}
