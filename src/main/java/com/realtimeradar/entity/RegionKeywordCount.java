package com.realtimeradar.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionKeywordCount {
    private String region;
    private int count;
}
