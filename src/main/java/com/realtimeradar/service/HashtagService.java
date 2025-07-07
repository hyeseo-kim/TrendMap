package com.realtimeradar.service;

import com.realtimeradar.repository.HashtagRepository;
import com.realtimeradar.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final KeywordRepository keywordRepository;

    public List<String> getHotHashtagsByRegion(String region) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1); // 최근 1시간 기준
        return hashtagRepository.findTopTagsByRegionAndTime(region, oneHourAgo);
    }

    public List<String> getTop10Hashtags(String region) {
        return hashtagRepository.findTop10TagsByRegion(region).stream()
                .distinct()
                .limit(10)
                .toList();
    }



}
