package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.service.generic.GenericService;

public interface UserService extends GenericService<User, Integer> {
    public User validatePassword(User user, String recentPassword, String newPassword, String confirmPassword);
    public User authenticate(String username, String password);
    public User verifyUser(String guidString);
}
