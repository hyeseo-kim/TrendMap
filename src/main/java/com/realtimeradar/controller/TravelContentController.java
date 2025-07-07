package com.realtimeradar.controller;

import com.realtimeradar.entity.Place;
import com.realtimeradar.entity.TravelCategory;
import com.realtimeradar.repository.PlaceRepository;
import com.realtimeradar.repository.TravelCategoryRepository;
import com.realtimeradar.service.TravelCategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class TravelContentController {

    private final PlaceRepository placeRepository;
    private final TravelCategoryRepository travelCategoryRepository;
    private final TravelCategoryService service;
    private final TravelCategoryService travelCategoryService;

    @GetMapping("/travel-content")
    public String showTravelContent(
            @RequestParam(name = "region", required = false) String regionParam,
            Model model,
            HttpSession session) {

        if (regionParam == null || regionParam.isBlank()) {
            regionParam = "수원";
        }

        List<String> recentRegions = (List<String>) session.getAttribute("recentRegions");
        if (recentRegions == null) {
            recentRegions = new ArrayList<>();
        }

        if (!regionParam.equals("전체")) {
            recentRegions.remove(regionParam);
            recentRegions.add(0, regionParam);

            if (recentRegions.size() > 5) {
                recentRegions = new ArrayList<>(recentRegions.subList(0, 5));
            }

            session.setAttribute("recentRegions", recentRegions);
        }

        String region = switch (regionParam) {
            case "서울" -> "seoul";
            case "수원" -> "suwon";
            case "인천" -> "incheon";
            case "광주" -> "gwangju";
            case "전주" -> "jeonju";
            case "장성" -> "jangseong";
            case "부산" -> "busan";
            case "대구" -> "daegu";
            case "창원" -> "changwon";
            case "대전" -> "daejeon";
            case "제주" -> "jeju";
            default -> "default";
        };

        String regionFullName = switch (regionParam) {
            case "서울" -> "서울특별시";
            case "수원" -> "경기도 수원시";
            case "인천" -> "인천광역시";
            case "광주" -> "광주광역시";
            case "전주" -> "전북특별자치도 전주시";
            case "장성" -> "전라남도 장성군";
            case "부산" -> "부산광역시";
            case "대구" -> "대구광역시";
            case "창원" -> "경상남도 창원시";
            case "대전" -> "대전광역시";
            case "제주" -> "제주특별자치도";
            default -> "대한민국";
        };

        List<Place> places = regionParam.equals("전체")
                ? placeRepository.findAll()
                : placeRepository.findByRegion(regionParam);

        List<TravelCategory> travelCategories = travelCategoryRepository.findAll();
        List<TravelCategory> popularContents = travelCategoryService.getPopularContents(); // 🔥 추가

        List<String> regionList = List.of("서울", "수원", "인천", "광주", "전주", "장성", "부산", "대구", "창원", "대전", "제주");


        model.addAttribute("region", region);
        model.addAttribute("regionParam", regionParam);
        model.addAttribute("regionFullName", regionFullName);
        model.addAttribute("places", places);
        model.addAttribute("travelCategories", travelCategories);
        model.addAttribute("regionList", regionList);
        model.addAttribute("recentRegions", recentRegions);
        model.addAttribute("popularContents", popularContents);

        return "travel-content";
    }

    @GetMapping("/travel-content/recent/delete")
    public String deleteRecentRegion(
            @RequestParam("region") String regionToDelete,
            @RequestParam(value = "current", required = false, defaultValue = "전체") String currentRegion,
            HttpSession session) {

        List<String> recentRegions = (List<String>) session.getAttribute("recentRegions");
        if (recentRegions != null) {
            recentRegions.remove(regionToDelete);
            session.setAttribute("recentRegions", recentRegions);
        }

        String encodedRegion = URLEncoder.encode(currentRegion, StandardCharsets.UTF_8);
        return "redirect:/travel-content?region=" + encodedRegion;
    }

    @PostMapping("/content/click/{id}")
    @ResponseBody
    public ResponseEntity<Void> recordClick(@PathVariable Long id) {
        travelCategoryService.recordClick(id);
        return ResponseEntity.ok().build();
    }

}
