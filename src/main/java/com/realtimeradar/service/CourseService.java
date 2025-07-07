package com.realtimeradar.service;

import com.realtimeradar.dto.CourseDTO;
import com.realtimeradar.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    Page<CourseDTO> searchCourses(String region, String tag, Pageable pageable, String sort);
    Course getCourseById(Long id);
}
