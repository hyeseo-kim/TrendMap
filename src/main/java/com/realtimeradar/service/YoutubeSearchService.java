package com.realtimeradar.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@SuppressWarnings("unchecked")
@Service
public class YoutubeSearchService {

    private final String API_KEY = "AIzaSyDkGAzjASQ7feLdVZ8yyJTlL9zH8TJIeMo"; // 🔑 실제 API 키로 교체

    private final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/search";

    public List<Map<String, String>> searchYoutube(String query) {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_URL)
                .queryParam("part", "snippet")
                .queryParam("q", query)
                .queryParam("key", API_KEY)
                .queryParam("maxResults", 6)
                .queryParam("type", "video")
                .build().toUriString();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        List<Map<String, String>> resultList = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

            for (Map<String, Object> item : items) {
                Map<String, Object> id = (Map<String, Object>) item.get("id");
                Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                Map<String, Object> thumbnails = (Map<String, Object>) ((Map<String, Object>) snippet.get("thumbnails")).get("default");

                String videoId = (String) id.get("videoId");
                String title = (String) snippet.get("title");
                String thumbnailUrl = (String) thumbnails.get("url");

                Map<String, String> videoData = new HashMap<>();
                videoData.put("videoId", videoId);
                videoData.put("title", title);
                videoData.put("thumbnailUrl", thumbnailUrl);
                videoData.put("videoUrl", "https://www.youtube.com/watch?v=" + videoId);

                resultList.add(videoData);
            }
        }

        return resultList;
    }
}
