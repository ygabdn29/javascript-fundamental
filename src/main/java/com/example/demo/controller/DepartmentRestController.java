package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.handler.Utils;
import com.example.demo.model.Department;
import com.example.demo.model.Region;
import com.example.demo.model.dto.SaveDepartmentDTO;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.RegionService;

@RestController
@RequestMapping("api")
public class DepartmentRestController {
  @Autowired
  private DepartmentService departmentService;
  @Autowired
  private RegionService regionService;

  @GetMapping("departments")
  public ResponseEntity<Object> index(){
    List<Department> departments = departmentService.get();
    return Utils.generateResponseEntity(HttpStatus.OK, "Retrieved", departments);
  }

  @PostMapping("department/save")
  public ResponseEntity<Object> saveRegion(@RequestBody SaveDepartmentDTO departmentDTO){
  Region selectedRegion = regionService.get(departmentDTO.getRegionId());

  Department department = new Department(departmentDTO.getId(), departmentDTO.getName(), selectedRegion);
  departmentService.save(department);
  return Utils.generateResponseEntity(HttpStatus.OK, "Department Saved!");
  }

  @DeleteMapping("department/{id}/delete")
  public String departmentRegion(@PathVariable Integer id){
    return departmentService.delete(id) ? "Department Deleted" : "Department cannot be deleted";
  }
}
