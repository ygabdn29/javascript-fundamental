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
    public ResponseEntity<Object> index(@RequestHeader("x-token-key") String token) {
        if(token.equals("Abcdefgh")){
            List<Region> regions = regionService.get();
            return Utils.generateResponseEntity(HttpStatus.OK, "Data has been retrieved", regions);
        }
        return Utils.generateResponseEntity(HttpStatus.BAD_REQUEST, "Data can't be access, token is not valid");
    }

    @GetMapping("region/{id}")
    public Region getId(@PathVariable Integer id) {
        Region region = regionService.get(id);
        return region;
    }

    @PostMapping("region/save")
    public Boolean saveRegion(@RequestBody Region region) {
        return regionService.save(region);
    }

    @DeleteMapping("region/delete/{id}")
    public Boolean deleteRegion(@PathVariable Integer id){
        return regionService.delete(id);
    }
}
