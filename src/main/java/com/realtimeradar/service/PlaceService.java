package com.realtimeradar.service;

import com.realtimeradar.dto.PlaceDTO;
import com.realtimeradar.entity.Place;

import java.util.List;

public interface PlaceService {
    List<PlaceDTO> getPlacesByRegion(String region);
}