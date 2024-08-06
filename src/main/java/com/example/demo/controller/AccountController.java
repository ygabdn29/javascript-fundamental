package com.example.demo.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

  @Autowired
  private PasswordEncoder passwordEncoder;

  @GetMapping("formlogin")
  public String index(Model model, HttpSession session) {
    // Check if user is already logged in
    // User loggedInUser = (User) session.getAttribute("user");
    // if (loggedInUser != null) {
    //   model.addAttribute("user", loggedInUser);
    //   return "login/welcome"; // If logged in, redirect to welcome page
    // }
    model.addAttribute("users", userService.get());
    return "login/indexlogin";
  }

  @PostMapping("login")
  public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
    User user = userService.authenticate(username, password); // Authenticate user
    // List<User> users = userService.get();
    // List<Role> roles = roleService.get();

    // if (user != null) {
    //   session.setAttribute("user", user); // Store user in session
    //   String sessionId = session.getId();
    //   Integer setId = user.getId();
    //   return "redirect:welcome/" + setId + ";jsessionid=" + sessionId; // Redirect with user ID and session ID
    // } else {
    //   model.addAttribute("error", "Invalid username or password");
    //   return "login/indexlogin"; // Redirect back to login page
    // }

    try{
      org.springframework.security.core.userdetails.User userLogin = new org.springframework.security.core.userdetails.User(user.getId().toString(), " ", getAuthorities(user.getRole().getName()));

      PreAuthenticatedAuthenticationToken authenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(userLogin," ", userLogin.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authenticatedAuthenticationToken);
      session.setAttribute("user", user);
      return "redirect:welcome";
    } catch (Exception e){
      return "login/indexlogin";
    }
  }

  public static Collection<? extends GrantedAuthority> getAuthorities(String role){
    final List<SimpleGrantedAuthority> authorities = new LinkedList<>();
    authorities.add(new SimpleGrantedAuthority(role));
    return authorities;
  }

  @GetMapping("welcome")
  public String welcome( Model model, HttpSession session) {
    User loggedInUser = (User) session.getAttribute("user");
    if (loggedInUser == null) {
      return "redirect:formlogin";
    }
    model.addAttribute("user", loggedInUser);
    model.addAttribute("userId", loggedInUser.getId());
    return "login/welcome";
  }

  @GetMapping("session/{userId}")
  public String getSessionId(@PathVariable Integer userId, Model model, HttpSession session) {
    User loggedInUser = (User) session.getAttribute("user");
    if (loggedInUser == null || !loggedInUser.getId().equals(userId)) {
      return "redirect:indexlogin";
    }
    String sessionId = session.getId();
    model.addAttribute("sessionId", sessionId);
    model.addAttribute("userId", userId);
    return "login/session";
  }

  @GetMapping("role")
  public String roleIndex(Model model) {
    model.addAttribute("users", userService.get());

    return "roleManagement/roleManagement";
  }

  @GetMapping("find-email")
  public String formEmail(Model model) {
    return "account/formForgotPassword";
  }

  @PostMapping("forgot-password")
  public String processForgotPassword(String email, Model model) {
    Employee employee = employeeService.findByEmail(email);
    if (employee == null) {
      model.addAttribute("error", "Username not found");
      return "account/formForgotPassword";
    }
    model.addAttribute("email", email);
    return "account/resetPassword";
  }

  @GetMapping("register")
  public String register(Model model) {
    model.addAttribute("employee", new Employee());
    model.addAttribute("user", new User());
    return "account/register";
  }

  @PostMapping("save")
  public String save(User user) {
    Role defaultRole = roleService.getRoleWithLowestLevel(); // EMPLOYEE ROLE (LOWEST LEVEL)
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    employeeService.save(user.getEmployee());
    user.setRole(defaultRole);
    return userService.save(user) ? "redirect:/account/formlogin" : "account/register";
  }

  @GetMapping("{id}/role")
  public String roleEdit(@PathVariable Integer id, Model model) {
    model.addAttribute("user", userService.get(id));
    model.addAttribute("roles", roleService.get());

    return "roleManagement/roleUpdate";
  }

  @PostMapping("role/update")
  public String roleUpdate(User user) {
    if (user.getId() != null) {
      return userService.save(user) ? "redirect:/account/role" : "error";
    }
    return "error";
  }

  @PostMapping("reset-password")
  public String processResetPassword(String email, String password, Model model) {
    Employee employee = employeeService.findByEmail(email);
    if (employee == null) {
      model.addAttribute("error", "Invalid email.");
      return "account/resetPassword";
    }

    Integer employeeId = employee.getId();
    User user = userService.get(employeeId);
    if (user == null) {
      model.addAttribute("error", "User not found.");
      return "account/resetPassword";
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userService.save(user);

    return "login/indexlogin";
  }

  @GetMapping("password/change")
  public String form(Model model, HttpSession session) {
    User loggedInUser = (User) session.getAttribute("user");
    model.addAttribute("user", loggedInUser);
    return "account/changePassword";
  }

  @PostMapping("password/change/success")
  public String changePassword(@RequestParam("recentPass") String recentPass,
      @RequestParam("newPass") String newPass,
      @RequestParam("confirmPass") String confirmPass,
      Model model, RedirectAttributes redirectAttributes, HttpSession session) {

    User loggedInUser = (User) session.getAttribute("user");
    User validatedUser = userService.validatePassword(loggedInUser, recentPass, newPass, confirmPass);

    if (validatedUser == null) {
      redirectAttributes.addFlashAttribute("errorMsg", "Invalid Password or Unmatch Password. Please try again!");
      return "redirect:/account/password/change/";
    }

    validatedUser.setPassword(newPass);
    userService.save(validatedUser);

    session.invalidate();
    redirectAttributes.addFlashAttribute("successMsg", "Your password has been successfully changed.");
    return "redirect:/account/formlogin";
  }
}
