package com.realtimeradar.dto;

import lombok.Data;
import java.util.List;

@Data
public class NewsApiResponse {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<NewsDTO> items;
}
