package com.realtimeradar.repository;
import com.realtimeradar.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByRegion(String region);
}