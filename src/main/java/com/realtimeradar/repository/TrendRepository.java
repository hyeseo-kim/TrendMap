package com.realtimeradar.repository;

import com.realtimeradar.entity.TrendingKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrendRepository extends JpaRepository<TrendingKeyword, Long> {
    List<TrendingKeyword> findTop10ByOrderByDateDesc();
}
