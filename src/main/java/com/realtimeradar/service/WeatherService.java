package com.realtimeradar.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WeatherService {

    @Value("${openweather.api.key}")
    private String apiKey;

    private static final Map<String, String> descriptionMap = Map.ofEntries(
            Map.entry("clear sky", "맑음"),
            Map.entry("few clouds", "약간의 구름"),
            Map.entry("scattered clouds", "흩어진 구름"),
            Map.entry("broken clouds", "구름 많음"),
            Map.entry("overcast clouds", "흐림"),
            Map.entry("shower rain", "소나기"),
            Map.entry("light rain", "약한 비"),
            Map.entry("moderate rain", "보통 비"),
            Map.entry("heavy intensity rain", "강한 비"),
            Map.entry("rain", "비"),
            Map.entry("thunderstorm", "뇌우"),
            Map.entry("snow", "눈"),
            Map.entry("mist", "안개")
    );

    public String getWeather(String cityName) {
        try {
            String urlStr = "https://api.openweathermap.org/data/2.5/weather?q=" +
                    URLEncoder.encode(cityName + ",KR", StandardCharsets.UTF_8) +
                    "&appid=" + apiKey +
                    "&units=metric&lang=ko";  // 'lang=ko' 는 일부 지역에서 무시됨

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            String result = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining());

            JSONObject json = new JSONObject(result);
            double temp = json.getJSONObject("main").getDouble("temp");
            String descEn = json.getJSONArray("weather").getJSONObject(0).getString("description");
            String descKo = descriptionMap.getOrDefault(descEn.toLowerCase(), descEn); // 영어 → 한글 매핑

            return Math.round(temp) + "°C / " + descKo;

        } catch (Exception e) {
            log.warn("❌ 날씨 정보 불러오기 실패: {}", e.getMessage());
            return "날씨 정보를 불러올 수 없음";
        }
    }

    public String getWeatherIcon(String weatherInfo) {
        if (weatherInfo == null) return "default.png";

        // 지역 변수로 소문자 변환
        String info = weatherInfo.toLowerCase();

        // 날씨 키워드 기반 조건 분기
        if (info.contains("맑음") || info.contains("clear")) return "sunny.png";
        if (info.contains("흐림") || info.contains("cloudy")) return "cloudy.png";
        if (info.contains("구름") || info.contains("cloud")) return "partly_cloudy.png";
        if (info.contains("비") || info.contains("rain")) return "rain.png";
        if (info.contains("눈") || info.contains("snow")) return "snow.png";
        if (info.contains("번개") || info.contains("thunder")) return "thunder.png";
        if (info.contains("안개") || info.contains("fog")) return "fog.png";

        return "default.png";
    }

}
