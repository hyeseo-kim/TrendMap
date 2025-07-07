package com.realtimeradar.repository;

import com.realtimeradar.entity.CourseLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseLocationRepository extends JpaRepository<CourseLocation, Long> {
    List<CourseLocation> findByCourseIdOrderByStepAsc(Long courseId);
}
