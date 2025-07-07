package com.realtimeradar.repository;

import com.realtimeradar.entity.TravelCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TravelCategoryRepository extends JpaRepository<TravelCategory, Long> {

    @Modifying
    @Query("UPDATE TravelCategory c SET c.clickCount = c.clickCount + 1 WHERE c.id = :id")
    void incrementClickCount(@Param("id") Long id);
}
