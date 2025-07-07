package com.realtimeradar.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private Long id;
    private String nickname;
    private String title;
    private String content;
    private String region;
    private String category;
    private MultipartFile image;
    private String imagePath;
    private int views;
    private int commentCount;
    private LocalDateTime createdAt;
    private int likes;
    private int dislikes;

}
