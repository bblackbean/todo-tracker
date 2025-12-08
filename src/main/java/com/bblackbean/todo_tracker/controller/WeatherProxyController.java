package com.bblackbean.todo_tracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WeatherProxyController {

    @Value("${openweathermap.api.key}")
    private String weatherApiKey;

    // final로 선언하여 @RequiredArgsConstructor가 생성자를 만들도록 함
    private final RestTemplate restTemplate;

    @GetMapping("/weather")
    public ResponseEntity<String> getCurrentWeather(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        log.info("Fetching weather for lat: {}, lon: {}", lat, lon);

        String url = "https://api.openweathermap.org/data/2.5/weather";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("lat", lat)     // 위도
                .queryParam("lon", lon)     // 경도
                .queryParam("appid", weatherApiKey)
                .queryParam("units", "metric") // 섭씨온도 사용
                .queryParam("lang", "kr");     // 한국어 설명 사용

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("Error fetching weather data", e); // 에러 로그 기록
            return ResponseEntity.status(500).body("Error fetching weather data.");
        }
    }
}
