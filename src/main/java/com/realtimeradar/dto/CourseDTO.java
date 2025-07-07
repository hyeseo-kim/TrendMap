package com.realtimeradar.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDTO {
    private Long id;
    private String title;
    private String region;
    private String tag;
    private double distanceKm;
    private String thumbnailUrl;
    private int views;

}
