package com.realtimeradar.controller;

import com.realtimeradar.dto.HotelDTO;
import com.realtimeradar.service.HotelSearchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HotelController {

    private final HotelSearchService hotelSearchService;

    public HotelController(HotelSearchService hotelSearchService) {
        this.hotelSearchService = hotelSearchService;
    }

    @GetMapping("/hotels")
    public String showHotelSearchPage(@RequestParam(required = false) String query,
                                      @RequestParam(required = false) String region,
                                      @RequestParam(required = false) String tag,
                                      @RequestParam(required = false) String category,
                                      Model model,
                                      HttpSession session) {

        if (region == null || region.isBlank()) region = "수원";
        if (category == null || category.isBlank()) category = "호텔"; // 기본값

        List<String> recentRegions = (List<String>) session.getAttribute("recentRegions");
        if (recentRegions == null) recentRegions = new ArrayList<>();
        if (!region.equals("전체")) {
            recentRegions.remove(region);
            recentRegions.add(0, region);
            if (recentRegions.size() > 5) {
                recentRegions = recentRegions.subList(0, 5);
            }
            session.setAttribute("recentRegions", recentRegions);
        }

        List<String> regionList = List.of("서울", "수원", "인천", "광주", "전주", "장성", "부산", "대구", "창원", "대전", "제주");

        List<HotelDTO> hotels = hotelSearchService.searchHotels(query, region, tag, category);

        hotels.removeIf(h ->
                h.getImageUrl() == null ||
                        h.getImageUrl().isBlank() ||
                        !h.getImageUrl().startsWith("http") ||
                        h.getImageUrl().toLowerCase().contains("noimage") ||
                        h.getImageUrl().toLowerCase().contains("hotel") ||  // alt="호텔 이미지" 방지
                        h.getImageUrl().toLowerCase().contains("default")   // 기본 이미지 제거 (선택사항)
        );

        List<HotelDTO> popularContents = hotelSearchService.getPopularHotels();

        model.addAttribute("query", query);
        model.addAttribute("regionParam", region);
        model.addAttribute("regionList", regionList);
        model.addAttribute("recentRegions", recentRegions);
        model.addAttribute("hotels", hotels);
        model.addAttribute("popularContents", popularContents);
        model.addAttribute("selectedTag", tag);
        model.addAttribute("selectedCategory", category);

        return "hotel-search";
    }

    @PostMapping("/hotels/click/{placeId}")
    @ResponseBody
    public ResponseEntity<Void> recordClick(@PathVariable String placeId,
                                            @RequestParam String name,
                                            @RequestParam String mapUrl,
                                            @RequestParam String region,
                                            @RequestParam String category) {

        hotelSearchService.recordClick(placeId, name, mapUrl, region, category);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hotels/recent/delete")
    public String deleteRecentRegion(@RequestParam String region,
                                     @RequestParam(defaultValue = "수원") String current,
                                     HttpSession session) {

        List<String> recentRegions = (List<String>) session.getAttribute("recentRegions");
        if (recentRegions != null) {
            recentRegions.remove(region);
            session.setAttribute("recentRegions", recentRegions);
        }

        return "redirect:/hotels?region=" + URLEncoder.encode(current, StandardCharsets.UTF_8);
    }
}
