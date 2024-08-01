package com.example.demo.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> get() {
        return roleRepository.findAll();
    }

    @Override
    public Role get(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean save(Role entity) {
        roleRepository.save(entity);
        return roleRepository.findById(entity.getId()).isPresent();
    }

    @Override
    public Boolean delete(Integer id) {
        roleRepository.deleteById(id);
        return roleRepository.findById(id).isEmpty();
    }

}
