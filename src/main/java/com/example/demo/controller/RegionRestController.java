package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("region")
    public ResponseEntity<Object> getRegionsList(@RequestHeader String X_Token){
        if(X_Token.equals("Test Token") ){
            List<Region> regions = regionService.get();
            return Utils.generateResponseEntity(HttpStatus.OK, "Data Berhasil di Ambil", regions);
        }
        
        return Utils.generateResponseEntity(HttpStatus.FORBIDDEN, X_Token, X_Token);
    }

      @GetMapping("getIDs")
    public List<Integer> getRegionIDs() {
        List<Region> regionsList = regionService.get();
        List<Integer> ids = new ArrayList<>();
        for (Region region : regionsList) {
            ids.add(region.getId());
        }
        return ids;
    }

    @PostMapping("save")
    public Boolean save(@RequestBody Region region){
        return regionService.save(region);
    }

    @PostMapping("delete")
    public Boolean delete(@RequestBody Region region) {
        Integer regionId = region.getId();
        return regionService.delete(regionId);
    }

    @PutMapping("update")
    public boolean update(@RequestBody Region region) {
        Integer regionID = region.getId();
        if (regionService.get(regionID) != null) {
            return regionService.save(region);
        }
        return false; 
    }
}
