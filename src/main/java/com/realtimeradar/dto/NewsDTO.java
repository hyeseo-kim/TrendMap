package com.realtimeradar.dto;

import lombok.Data;

@Data
public class NewsDTO {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;
}
