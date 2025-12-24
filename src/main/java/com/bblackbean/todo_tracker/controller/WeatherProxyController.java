package com.bblackbean.todo_tracker.controller;

import com.bblackbean.todo_tracker.service.WeatherService;
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

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<String> getCurrentWeather(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        log.info("Fetching weather for lat: {}, lon: {}", lat, lon);
        try {
            String body = weatherService.getCurrentWeather(lat, lon);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Error fetching weather data", e); // 에러 로그 기록
            return ResponseEntity.status(500).body("Error fetching weather data.");
        }
    }
}
