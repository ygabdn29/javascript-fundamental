package com.example.demo.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
import com.example.demo.model.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("api/account")
public class AccountRestController {
  @Autowired
  private UserService userService;
  @Autowired
  private EmailService emailService;
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

  
}
