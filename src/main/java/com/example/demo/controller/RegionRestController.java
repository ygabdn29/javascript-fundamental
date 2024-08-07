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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.handler.Utils;
import com.example.demo.model.Region;
import com.example.demo.service.RegionService;

@RestController
@RequestMapping("api")
public class RegionRestController {
  @Autowired
  private RegionService regionService;

  @GetMapping("regions")
  public ResponseEntity<Object>index(@RequestHeader("x-access-token") String token){
    if(token.equals("FGH-CVB-BNM")){
      List<Region> regions = regionService.get();
      return Utils.generaResponseEntity(HttpStatus.OK, "Retrieved", regions);
    }
    // Token ini masuknya bad request
    return Utils.generaResponseEntity(HttpStatus.BAD_REQUEST, "Token NOT Valid!");
  }

  @GetMapping("region/{id}")
  public Region getById(@PathVariable Integer id){
    Region region = regionService.get(id);
    return region;
  }

  @PostMapping("region/save")
  public ResponseEntity<String> saveRegion(@RequestBody Region region){
    regionService.save(region);
    return ResponseEntity.ok("Region Saved!");
  }

  // @PostMapping("region/{id}/update")
  // public String updateRegion(@PathVariable Integer id, @RequestBody Region region){
  //   Region selectedRegion = regionService.get(id);
  //   // updatedRegion.setName(region.getName());
  //   // regionService.save(updatedRegion);
  //   // return ResponseEntity.ok("Region Updated!");
  //   Boolean result = false;
  //   if(selectedRegion.getId() == region.getId()){
  //     result = regionService.save(region);
  //   }
  //   return result ? "Region Updated!" : "Cannot Update";
  // }

  @DeleteMapping("region/{id}/delete")
  public String deleteRegion(@PathVariable Integer id){
    return regionService.delete(id) ? "Region Deleted" : "Region cannot be deleted";
  }


}