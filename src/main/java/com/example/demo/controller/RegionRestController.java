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
    public ResponseEntity<Object> index(@RequestHeader String token) {
        if(token.equals("test") ){
            List<Region> regions = regionService.get();
            return Utils.generateResponseEntity(HttpStatus.OK, "Data has been retrieved", regions);
        }
        return Utils.generateResponseEntity(HttpStatus.BAD_REQUEST, "Token is not valid");
    }

    @GetMapping("region/{id}")
    public Region getRegion(@PathVariable Integer id) {
        Region region = regionService.get(id);
        return region;
    }
    
    @PostMapping("save")
    public String save(@RequestBody Region region) {
        return regionService.save(region)? "Data added successfully" : "Failed to add data";
    }
    
    // @PutMapping("update/{id}")
    // public String update(@PathVariable Integer id, @RequestBody Region newRegion) {
    //     Region region = regionService.get(id);
    //     Boolean result = false;
    //     if(region.getId() == newRegion.getId()){
    //         result = regionService.save(newRegion);
    //     }        
    //     return result? "Data updated successfully" : "Failed to update data";
    // }
    

    @DeleteMapping("delete/{id}")
    public String delete(@PathVariable Integer id) {
        return regionService.delete(id) ? "Data sucessfully deleted" : "Data not found";
    }
    

    
    
}
