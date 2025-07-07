package com.realtimeradar.repository;

import com.realtimeradar.entity.HotelClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelClickRepository extends JpaRepository<HotelClick, String> {
    List<HotelClick> findTop5ByOrderByClickCountDesc();
}
