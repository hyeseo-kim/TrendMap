package com.realtimeradar.controller;

import com.realtimeradar.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping("/hashtags")
    public String showHashtags(@RequestParam String region, Model model) {
        model.addAttribute("hotHashtags", hashtagService.getHotHashtagsByRegion(region));
        return "home";
    }
}
