package com.realtimeradar.controller;

import com.realtimeradar.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/click-news")
    public String clickNews(@RequestParam Long newsId, @RequestParam String region) {
        newsService.incrementClickCount(newsId);
        return "redirect:/realtime-news?region=" + region;
    }
}
