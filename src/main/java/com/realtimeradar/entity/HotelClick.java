package com.realtimeradar.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelClick {
    @Id
    private String placeId;

    private String name;
    private String mapUrl;
    private int clickCount;
    private String region;
    private String category;
}
