package com.realtimeradar.repository;

import com.realtimeradar.entity.Hashtag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Query("SELECT h.tag FROM Hashtag h WHERE h.region = :region AND h.crawledAt >= :since GROUP BY h.tag ORDER BY SUM(h.count) DESC")
    List<String> findTopTagsByRegionAndTime(@Param("region") String region, @Param("since") LocalDateTime since);

    @Query("SELECT h.tag FROM Hashtag h WHERE h.region = :region ORDER BY h.count DESC LIMIT 10")
    List<String> findTop10TagsByRegion(@Param("region") String region);

}
