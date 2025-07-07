package com.realtimeradar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String region;
    private String tag;
    private double distanceKm;
    private String thumbnailUrl;
    private String schedule;
    private String theme;
    private Double latitude;
    private Double longitude;

    @Column(length = 3000)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String detailHtml;

    @Column(nullable = false)
    private int likes;

    @Column(nullable = false)
    private int views;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseLocation> locations = new ArrayList<>();
}
