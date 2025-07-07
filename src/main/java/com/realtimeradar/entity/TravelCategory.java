package com.realtimeradar.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "travel_category")
public class TravelCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String region;
    private String category;
    private String title;
    private String imageUrl;
    private String link;
    private String source;
    private int clickCount = 0;

}
