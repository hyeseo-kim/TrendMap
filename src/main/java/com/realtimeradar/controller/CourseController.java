package com.realtimeradar.controller;
import com.realtimeradar.dto.CourseDTO;
import com.realtimeradar.entity.Course;
import com.realtimeradar.entity.CourseLocation;
import com.realtimeradar.repository.CourseLocationRepository;
import com.realtimeradar.repository.CourseRepository;
import com.realtimeradar.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final CourseLocationRepository courseLocationRepository;

    @GetMapping("/course/list")
    public String courseList(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) String region,
                             @RequestParam(required = false) String tag,
                             @RequestParam(defaultValue = "latest") String sort) {

        List<String> regionList = List.of("전체", "서울", "수원", "인천", "광주", "전주", "장성", "부산", "대구", "창원", "대전", "제주");

        List<String> tagList = List.of("전체", "자연코스", "#데이트코스", "자녀와함께", "가족코스", "연인코스", "친구와함께", "#힐링코스", "#DMZ평화관광", "한국관광100선", "관광두레", "야간관광");

        Page<CourseDTO> courses = courseService.searchCourses(
                region != null && !region.equals("전체") ? region : null,
                tag,
                PageRequest.of(page, 7),
                sort
        );


        model.addAttribute("courses", courses);
        model.addAttribute("regionList", regionList);
        model.addAttribute("tagList", tagList);
        model.addAttribute("selectedRegion", region);
        model.addAttribute("selectedTag", tag);
        model.addAttribute("sort", sort);

        return "course/course-list";
    }

    @GetMapping("/course/detail/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id).orElseThrow();

        course.setViews(course.getViews() + 1);
        courseRepository.save(course);

        List<CourseLocation> locations = courseLocationRepository.findByCourseIdOrderByStepAsc(id);

        model.addAttribute("course", course);
        model.addAttribute("locations", locations);
        model.addAttribute("locationCount", locations.size()); 

        return "course/course-detail";
    }


    @PostMapping("/course/like/{id}")
    @ResponseBody
    public ResponseEntity<Integer> likeCourse(@PathVariable Long id) {
        Course course = courseRepository.findById(id).orElseThrow();
        course.setLikes(course.getLikes() + 1);
        courseRepository.save(course);
        return ResponseEntity.ok(course.getLikes());
    }

}
