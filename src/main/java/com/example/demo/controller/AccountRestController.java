package com.example.demo.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.example.demo.model.dto.UserDto;
import com.example.demo.service.EmailService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("api/account")
public class AccountRestController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

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

    @GetMapping("list")
    public ResponseEntity<Object> get(@RequestHeader("X-token-account-key") String token ){
        if(token.equals("123456789")){
            List<User> users = userService.get();
            return Utils.generateResponseEntity(HttpStatus.OK, "Data has been retrieved", users);
        }
        return Utils.generateResponseEntity(HttpStatus.BAD_REQUEST, "Data can't be accessed. Token is invalid");
    } 

    @PostMapping("role/save")
    public ResponseEntity<Object> save(@RequestHeader("X-token-account-key") String token, @RequestBody UserDto userDto){
        if(token.equals("123456789")){
            User user2 = userService.get(userDto.getEmployee_id());
            user2.setRole(roleService.get(userDto.getRole_id()));
            userService.save(user2);
            return Utils.generateResponseEntity(HttpStatus.OK, "Role has been assigned", userDto);
        }
        return Utils.generateResponseEntity(HttpStatus.BAD_REQUEST, "Role can't be assigned. Token is invalid");
    }



}
