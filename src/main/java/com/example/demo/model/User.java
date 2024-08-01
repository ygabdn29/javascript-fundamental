package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_m_user")
public class User {
  @Column(name = "password")
  private String password;

  @Column(name = "username")
  private String username;

  @ManyToOne
  @JoinColumn(name = "role_id", referencedColumnName = "id")
  private Role role;

  @OneToOne
  @JoinColumn(name = "id", referencedColumnName = "id")
  private Employee employee;

  
  public User(){

  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }
}
