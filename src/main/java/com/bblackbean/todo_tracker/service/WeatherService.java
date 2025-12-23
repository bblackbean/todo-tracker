package com.bblackbean.todo_tracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${openweathermap.api.key}")
    private String weatherApiKey;

    private final RestTemplate restTemplate;

    @Cacheable(cacheNames = "weather", key = "T(String).format('lat=%s:lon=%s:units=metric:lang=kr', #lat, #lon)")
    public String getCurrentWeather(double lat, double lon) {
        String url = "https://api.openweathermap.org/data/2.5/weather";
        String uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", weatherApiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "kr")
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        return response.getBody();
    }
}
