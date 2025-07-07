package com.realtimeradar.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String title;
    private String region;
    private String category;
    private String imagePath;

    @Column(length = 3000)
    private String content;

    @Column(nullable = false)
    private int views;

    @Column(nullable = false)
    private int commentCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int likes;

    @Column(nullable = false)
    private int dislikes;
}
