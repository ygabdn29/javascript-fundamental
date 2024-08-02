package com.example.demo.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("account")
public class AccountController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String user(Model model) {
        List<User> users = userService.get();
        model.addAttribute("users", users);
        return "account/index";
    }

    @GetMapping("password/change")
    public String form(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");
        model.addAttribute("user", loggedInUser);
        return "account/changePassword";
    }

    // @GetMapping("password/change/{id}")
    // public String form(@PathVariable(value = "id") Integer id, Model model) {
    // User user = userService.get(id);
    // model.addAttribute("user", user);
    // return "account/changePassword";
    // }

    @PostMapping("password/change/success")
    public String changePassword(@RequestParam("recentPass") String recentPass,
            @RequestParam("newPass") String newPass,
            @RequestParam("confirmPass") String confirmPass,
            Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");

        User validatedUser = userService.validatePassword(loggedInUser, recentPass, newPass, confirmPass);

        if (validatedUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Invalid Password or Unmatch Password");
            return "redirect:/account/password/change/";
        }

        validatedUser.setPassword(newPass);
        userService.save(validatedUser);

        return "redirect:/account";
    }

}
