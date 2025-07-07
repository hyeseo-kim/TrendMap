package com.realtimeradar.service;

import com.realtimeradar.crawler.HashtagCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogHashtagService {

    private final HashtagCrawler crawler;

    public List<String> getLiveHashtags(String region) {
        List<String> tags = crawler.crawlBlogHashtags(region);
        if (tags == null || tags.isEmpty()) {
            return List.of("#" + region + "핫플", "#맛집", "#카페", "#데이트", "#포토존");
        }
        return tags;
    }
}
