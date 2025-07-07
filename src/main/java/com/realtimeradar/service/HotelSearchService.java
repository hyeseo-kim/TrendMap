// ✅ HotelSearchService.java
package com.realtimeradar.service;

import com.realtimeradar.dto.HotelDTO;
import com.realtimeradar.entity.HotelClick;
import com.realtimeradar.repository.HotelClickRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@Slf4j
public class HotelSearchService {

    private final String GOOGLE_PLACE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";

    @Value("${google.api.key}")
    private String apiKey;

    private final HotelClickRepository hotelClickRepository;

    public HotelSearchService(HotelClickRepository hotelClickRepository) {
        this.hotelClickRepository = hotelClickRepository;
    }

    public List<HotelDTO> searchHotels(String query, String region, String tag, String category) {
        RestTemplate restTemplate = new RestTemplate();
        String simpleRegion = simplifyRegion(region);

        String fullQuery = simpleRegion + " " + (category != null ? category : "호텔");
        if (query != null && !query.isBlank()) fullQuery += " " + query;
        if (tag != null && !tag.isBlank()) fullQuery += " " + tag;

        String url = UriComponentsBuilder.fromHttpUrl(GOOGLE_PLACE_URL)
                .queryParam("query", fullQuery.trim())
                .queryParam("language", "ko")
                .queryParam("key", apiKey)
                .build().toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        List<HotelDTO> resultList = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
            for (Map<String, Object> item : results) {
                String name = (String) item.get("name");
                String address = (String) item.get("formatted_address");
                String rating = String.valueOf(item.getOrDefault("rating", "0"));
                String userRatingsTotal = String.valueOf(item.getOrDefault("user_ratings_total", "0"));
                String placeId = (String) item.get("place_id");
                String mapUrl = "https://www.google.com/maps/place/?q=place_id:" + placeId;

                String imageUrl = null;
                if (item.containsKey("photos")) {
                    List<Map<String, Object>> photos = (List<Map<String, Object>>) item.get("photos");
                    if (!photos.isEmpty()) {
                        String photoReference = (String) photos.get(0).get("photo_reference");
                        imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + photoReference + "&key=" + apiKey;

                    }
                }
                if (imageUrl == null || imageUrl.isBlank()) continue;

                // 클릭 수
                int clickCount = hotelClickRepository.findById(placeId)
                        .map(HotelClick::getClickCount).orElse(0);

                resultList.add(HotelDTO.builder()
                        .placeId(placeId)
                        .name(name)
                        .address(address)
                        .rating(rating)
                        .userRatingsTotal(userRatingsTotal)
                        .mapUrl(mapUrl)
                        .imageUrl(imageUrl)
                        .clickCount(clickCount)
                        .price("정보 없음")
                        .description("설명 없음")
                        .build());
            }
        }

        return resultList;
    }


    public void recordClick(String placeId, String name, String mapUrl, String region, String category) {
        HotelClick hotelClick = hotelClickRepository.findById(placeId)
                .orElse(HotelClick.builder()
                        .placeId(placeId)
                        .name(name)
                        .mapUrl(mapUrl)
                        .region(region)
                        .category(category)
                        .clickCount(0)
                        .build());

        hotelClick.setClickCount(hotelClick.getClickCount() + 1);
        hotelClickRepository.save(hotelClick);
        log.info("✅ 클릭 기록: {} - {}회", name, hotelClick.getClickCount());
    }

    public List<HotelDTO> getPopularHotels() {
        return hotelClickRepository.findTop5ByOrderByClickCountDesc().stream()
                .map(c -> HotelDTO.builder()
                        .placeId(c.getPlaceId())
                        .name(c.getName())
                        .mapUrl(c.getMapUrl())
                        .clickCount(c.getClickCount())
                        .build())
                .toList();
    }

    private String simplifyRegion(String fullRegion) {
        if (fullRegion == null || fullRegion.isBlank()) return "";
        return switch (fullRegion) {
            case "서울", "서울특별시" -> "서울";
            case "수원", "경기도 수원시" -> "수원";
            case "광주", "광주광역시" -> "광주";
            case "인천", "인천광역시" -> "인천";
            case "전주", "전북특별자치도 전주시" -> "전주";
            case "장성", "전라남도 장성군" -> "장성";
            case "부산", "부산광역시" -> "부산";
            case "대구", "대구광역시" -> "대구";
            case "창원", "경상남도 창원시" -> "창원";
            case "대전", "대전광역시" -> "대전";
            case "제주", "제주특별자치도" -> "제주";
            default -> fullRegion.replaceAll("[^가-힣]", "").replace("시", "");
        };
    }
}