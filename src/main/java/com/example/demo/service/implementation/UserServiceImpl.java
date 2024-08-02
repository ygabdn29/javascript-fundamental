package com.example.demo5.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo5.model.User;
import com.example.demo5.repository.UserRepository;
import com.example.demo5.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    // validasi untuk cek username dan pasword 
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
          return user;
        }
        return null;
      }

      @Override
      public List<User> get() {
        return userRepository.findAll();
      }
    
    //   @Override
    //   public User get(Integer key) {
    //     return userRepository.findById(key).orElse(null);
    //   }
}
