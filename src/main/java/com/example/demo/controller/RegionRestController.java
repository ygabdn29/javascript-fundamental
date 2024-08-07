package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.handler.Utils;
import com.example.demo.model.Region;
import com.example.demo.service.RegionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("api")
public class RegionRestController {
  @Autowired
  private RegionService regionService;

  @GetMapping("regions")
  public ResponseEntity<Object> get(@RequestHeader String tokenAuth) {
    if (tokenAuth.equals("nilamcantik")) {
      List<Region> regions = regionService.get();
      return Utils.generaResponseEntity(HttpStatus.OK, "Data Has Been Retrieved", regions);
    } else{
      return Utils.generaResponseEntity(HttpStatus.BAD_REQUEST, "Token Is Not Valid");
    }
  }

  @GetMapping("regions/{id}")
  public Region getById(@PathVariable Integer id) {
    Region region = regionService.get(id);
    return region;
  }

  @PostMapping("regions/save")
  public Boolean saveData(@RequestBody Region region) {
    return regionService.save(region);
  }

  @PostMapping("regions/delete/{id}")
  public Boolean delete(@PathVariable Integer id) {
    return regionService.delete(id);
  }

}
