package com.realtimeradar.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HotelDTO {
    private String placeId;
    private String name;
    private String address;
    private String rating;
    private String userRatingsTotal;
    private String mapUrl;
    private String imageUrl;
    private String price;
    private String description;
    private int clickCount;
}
