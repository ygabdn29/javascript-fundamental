package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.handler.Utils;
import com.example.demo.model.User;
import com.example.demo.model.dto.UserDto;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("api/account")
public class RoleRestController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

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
