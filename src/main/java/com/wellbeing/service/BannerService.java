package com.wellbeing.service;


import com.wellbeing.ExceptionHandler.ResourceNotFoundException;
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

    public List<BannerResponse> getByStatus(Boolean status) {

        return bannerRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

	public String updatebanner(String bannerId, BannerRequest bannerRequest) {
		
		Banner banner = bannerRepository.findById(bannerId)
				.orElseThrow(() -> new ResourceNotFoundException("Banner Not Found to Delete"));
		
		if(bannerRequest.getName() != null) {
			banner.setName(bannerRequest.getName());
		}
		
		if(bannerRequest.getDescription() != null) {
			banner.setDescription(bannerRequest.getDescription());
		}
		
		bannerRepository.save(banner);

		return "Banner updated succesfully";
	}

	public String changeStatus(String bannerId, Boolean status) {
		
		Banner banner = bannerRepository.findById(bannerId)
				.orElseThrow(() -> new ResourceNotFoundException("Banner Not Found to Update status"));
		
		banner.setStatus(status);
		bannerRepository.save(banner);

		return "Banner status updated Successfuly: "+status;
	}
}