package com.wellbeing.controller;


import com.wellbeing.dto.BannerRequest;
import com.wellbeing.dto.BannerResponse;
import com.wellbeing.service.BannerService;
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

    @GetMapping("/all")
    public ResponseEntity<List<BannerResponse>> getAllBanners() {

        return ResponseEntity.ok(
                bannerService.getAllBanners());
    }
}
