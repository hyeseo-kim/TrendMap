package com.realtimeradar.service;

import com.realtimeradar.dto.CourseDTO;
import com.realtimeradar.entity.Course;
import com.realtimeradar.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public Page<CourseDTO> searchCourses(String region, String tag, Pageable pageable, String sort) {
        Specification<Course> spec = (root, query, cb) -> cb.conjunction(); // always true

        if (region != null && !region.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("region"), region));
        }

        if (tag != null && !tag.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("tag"), "%" + tag + "%"));
        }

        Sort sortCondition = "popular".equals(sort)
                ? Sort.by(Sort.Direction.DESC, "views")
                : Sort.by(Sort.Direction.DESC, "id"); // 최신순은 id 기준

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortCondition);

        return courseRepository.findAll(spec, sortedPageable).map(this::toDTO);
    }

    private CourseDTO toDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .region(course.getRegion())
                .tag(course.getTag())
                .distanceKm(course.getDistanceKm())
                .thumbnailUrl(course.getThumbnailUrl())
                .build();
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 코스가 없습니다. ID=" + id));
    }

}
