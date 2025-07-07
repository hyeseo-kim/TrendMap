package com.realtimeradar.service;

import com.realtimeradar.entity.Keyword;
import com.realtimeradar.entity.RegionKeywordCount;
import com.realtimeradar.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public List<Keyword> getPopularKeywordsByRegion(String region) {
        return keywordRepository.findTop10ByRegionOrderByClickCountDesc(region);
    }

}
