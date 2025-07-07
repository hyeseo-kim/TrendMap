package com.realtimeradar.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;
    private String region;
    private int clickCount = 0;
    private String writtenTime;

    @Column(name = "crawled_at")
    private LocalDateTime crawledAt;

    @Column(length = 1000)
    private String summary;

    @Column(length = 255)
    private String publisher;

    @Column(length = 1000)
    private String imageUrl;

}
