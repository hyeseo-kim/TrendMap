package com.realtimeradar.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private String region;
    private String category;
    private String tag;
    private String link;
    private Double latitude;
    private Double longitude;
    private String categoryName;
}
