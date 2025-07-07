package com.realtimeradar.repository;

import com.realtimeradar.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    List<Keyword> findTop10ByRegionOrderByClickCountDesc(String region);

}
