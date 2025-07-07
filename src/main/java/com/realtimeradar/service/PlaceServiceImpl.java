package com.realtimeradar.service;

import com.realtimeradar.dto.PlaceDTO;
import com.realtimeradar.entity.Place;
import com.realtimeradar.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public List<PlaceDTO> getPlacesByRegion(String region) {
        List<Place> places = placeRepository.findByRegion(region);

        Map<String, String> categoryNameMap = new HashMap<>();
        categoryNameMap.put("kid", "아이와");
        categoryNameMap.put("date", "데이트");
        categoryNameMap.put("family", "가족과");
        categoryNameMap.put("food", "맛집");
        categoryNameMap.put("drink", "술집");
        categoryNameMap.put("photo", "포토존");
        categoryNameMap.put("cafe", "대형카페");

        return places.stream().map(place -> PlaceDTO.builder()
                .name(place.getName())
                .imageUrl(place.getImageUrl())
                .region(place.getRegion())
                .category(place.getCategory())
                .tag(place.getTag())
                .link(place.getLink())
                .categoryName(categoryNameMap.getOrDefault(place.getCategory(), "기타"))
                .build()).collect(Collectors.toList());
    }


}
