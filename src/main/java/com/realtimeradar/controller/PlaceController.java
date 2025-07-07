package com.realtimeradar.controller;

import com.realtimeradar.dto.PlaceDTO;
import com.realtimeradar.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/home")
    public String showHome(@RequestParam(defaultValue = "수원") String region, Model model) {
        List<PlaceDTO> places = placeService.getPlacesByRegion(region);
        model.addAttribute("places", places);
        model.addAttribute("region", region);
        return "home";
    }
}
