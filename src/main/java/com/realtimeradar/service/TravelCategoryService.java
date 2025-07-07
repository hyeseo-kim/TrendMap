package com.realtimeradar.service;

import com.realtimeradar.entity.TravelCategory;
import com.realtimeradar.repository.TravelCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelCategoryService {
    private final TravelCategoryRepository repository;

    @Transactional
    public void recordClick(Long id) {
        repository.incrementClickCount(id);
    }

    public List<TravelCategory> getPopularContents() {
        return repository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "clickCount"))).getContent();
    }
}