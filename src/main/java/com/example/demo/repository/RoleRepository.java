package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // @Query(value = "SELECT id FROM tb_m_role WHERE level = (SELECT MIN(level) FROM tb_m_role);")  
}
