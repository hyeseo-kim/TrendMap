package com.realtimeradar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // ✅ 추가

@SpringBootApplication
@EnableScheduling  // ✅ 이거 추가!
public class RadarApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadarApplication.class, args);
    }
}
