package com.realtimeradar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String location;
    private String region;
}
