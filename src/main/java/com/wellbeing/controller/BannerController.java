package com.wellbeing.controller;


import com.wellbeing.dto.BannerRequest;
import com.wellbeing.dto.BannerResponse;
import com.wellbeing.service.BannerService;

import ch.qos.logback.core.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banner")
@RequiredArgsConstructor
public class BannerController {


    private final BannerService bannerService;

    @PostMapping("/create")
    public ResponseEntity<BannerResponse> createBanner(@RequestBody BannerRequest request) {
        return ResponseEntity.ok(
                bannerService.createBanner(request));
    }

    @GetMapping("/getByStatus")
    public ResponseEntity<List<BannerResponse>> getByStatus(
    							@RequestParam (defaultValue = "true") Boolean status) {

        return ResponseEntity.ok(
                bannerService.getByStatus(status));
    }
    
    
    @PutMapping("/update")
    public String updateBanner(@RequestParam String bannerId,
    						@RequestBody BannerRequest bannerRequest) {
    	
    	return bannerService.updatebanner(bannerId, bannerRequest);
    }
    
    
    @PutMapping("/changeStatus")
    public String changeStatus(@RequestParam String bannerId,
    							@RequestParam Boolean status) {
    	
    	return bannerService.changeStatus(bannerId, status);
    }
}
