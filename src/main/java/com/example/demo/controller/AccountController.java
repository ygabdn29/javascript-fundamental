package com.example.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Employee;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;


@Controller
@RequestMapping("account")
public class AccountController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("testlogin")
    public String index(Model model){
    model.addAttribute("users", userService.get());

    return "login/indexlogin";

   }


//    @PostMapping("login")
//    public String login(@RequestParam String username, @RequestParam String password, Model model) {
//        User user = userService.authenticate(username, password);
//        if (user != null) {
//            model.addAttribute("user", user);

//            return "login/welcome"; // Redirect ke welcome page
//        } else {
//            model.addAttribute("error", "Invalid username or password");
//            return "login/indexlogin"; // Redirect balikin ke halaman login
//        }
//    }

   @GetMapping("register")
   public String register(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("user", new User());
        return "account/register";
   }

   @PostMapping("save")
   public String save(User user) {
        Role defaultRole = roleService.get(2); // EMPLOYEE ROLE (LOWEST LEVEL)        
        employeeService.save(user.getEmployee());
        // Integer id = user.getEmployee().getId();
        // System.out.println("ID : " + id);
        // System.out.println("Role name: " + defaultRole.getName());
        // user.setEmployee(employeeService.findById(id));        
        user.setRole(defaultRole);  
        // user.setId(id);
        return userService.save(user) ? "redirect:/account" : "account/register";
   }
   
   
//    @GetMapping("role")
//    public String roleIndex(){
    
//    }

}

