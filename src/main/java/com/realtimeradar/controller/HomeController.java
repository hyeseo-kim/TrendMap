package com.realtimeradar.controller;

import com.realtimeradar.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final KeywordService keywordService;

    @GetMapping("/")
    public String showHome(@RequestParam(defaultValue = "수원") String region, Model model) {
        model.addAttribute("topKeywords", keywordService.getPopularKeywordsByRegion(region));
        model.addAttribute("region", region);

        return "home";
    }
}
