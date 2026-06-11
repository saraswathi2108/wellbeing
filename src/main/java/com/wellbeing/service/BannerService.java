package com.wellbeing.service;


import com.wellbeing.dto.BannerRequest;
import com.wellbeing.dto.BannerResponse;
import com.wellbeing.entity.Banner;
import com.wellbeing.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerResponse createBanner(BannerRequest request) {
        Long count = bannerRepository.count() + 1;
        String bannerId = String.format("BAN%03d", count);
        Banner banner = new Banner();
        banner.setBannerId(bannerId);
        banner.setName(request.getName());
        banner.setDescription(request.getDescription());
        Banner savedBanner = bannerRepository.save(banner);
        return mapToResponse(savedBanner);
    }

    private BannerResponse mapToResponse(
            Banner banner) {

        BannerResponse response = new BannerResponse();
        response.setBannerId(banner.getBannerId());
        response.setName(banner.getName());
        response.setDescription(banner.getDescription());
        return response;
    }

    public List<BannerResponse> getAllBanners() {

        return bannerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}