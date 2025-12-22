package com.bblackbean.todo_tracker.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WeatherProxyController.class)
@TestPropertySource(properties = {"openweathermap.api.key=test-api-key"})
class WeatherProxyControllerTest {

    @Autowired
    MockMvc mockMvc;    // MockMvc : 가짜 postman

    @MockBean   // 이 RestTemplate은 진짜가 아니라, 조종할 수 있는 가짜(Mock) 객체로 만들 것을 요청
    RestTemplate restTemplate;

    // 성공 케이스
    @Test
    @WithMockUser
    void getCurrentWeather_success_returns200_andBody() throws Exception {
        // 날씨 정보를 정상적으로 잘 받아오는지 확인. 테스트는 보통 Give-When-Then 3단계로 구성됨
        
        // given (준비 단계) : 테스트에 필요한 환경 설정
        String upstreamJson = "{\"weather\":[{\"description\":\"맑음\"}],\"main\":{\"temp\":3.2}}";   // 가짜 데이터
        
        // Mockito라는 라이브러리를 사용해서 가짜 restTemplate 사용
        given(restTemplate.getForEntity(org.mockito.ArgumentMatchers.anyString(), eq(String.class)))
                .willReturn(new ResponseEntity<>(upstreamJson, HttpStatus.OK));
        // 만약 restTemplate의 getForEntity 메서드가 어떤 url이든 호출되면, 미리 만들어둔 upstreamJson 데이터를 담아서 정상응답(200) 반환하라고 설정

        // When (실행) : 실제 테스트하고 싶은 동작 수행
        mockMvc.perform(get("/api/weather")     // 가짜 브라우저(mockMvc)를 시켜 /api/weather url로 get 요청
                .param("lat", "37.5665")
                .param("lon", "126.9780"))
        // Then (검증) : 실행 결과가 예상한대로인지 확인
            .andExpect(status().isOk())         // 컨트롤러가 응답코드로 200 ok(성공)를 주는지 확인
            .andExpect(content().string(upstreamJson));     // 컨트롤러가 응답 내용물로 미리 준비해둔 upstreamJson 데이터를 그대로 전달하는지 확인

        // 호출 url에 파라미터가 잘 붙는지 검증 (추가 검증)
        // ArgumentCaptor는 restTemplate이 호출될 때 전달된 url 주소를 붙잡아두는 덫 역할 
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForEntity(urlCaptor.capture(), eq(String.class));   // restTemplate의 getForEntity가 정말로 한 번 호출되었는지 확인

        String calledUrl = urlCaptor.getValue();
        // 붙잡은 url 안에 api 주소, 위도, 경도, api 키 등이 정확히 포함되어있는지 하나씩 확인
        assertThat(calledUrl).contains("https://api.openweathermap.org/data/2.5/weather");
        assertThat(calledUrl).contains("lat=37.5665");
        assertThat(calledUrl).contains("lon=126.978");
        assertThat(calledUrl).contains("appid=test-api-key");
        assertThat(calledUrl).contains("units=metric");
        assertThat(calledUrl).contains("lang=kr");
    }

    // 잘못된 파라미터 케이스
    @Test
    @WithMockUser
    void getCurrentWeather_invalidParam_returns400() throws Exception {
        mockMvc.perform(get("/api/weather")
                .param("lat", "abc")    // 숫자가 아닌 잘못된 값
                .param("lon", "126.9780"))
            .andExpect(status().isBadRequest());    // 400 에러 기대
    }

    // 외부 api 오류 케이스
    @Test
    @WithMockUser
    void getCurrentWeather_restTemplateThrows_returns500_andMessage() throws Exception {
        // 날씨 api를 호출하다가 에러가 발생했을 때, 서버가 터지지 않고 사용자에게 적절한 에러 메시지를 주는지 확인

        // given (준비)
        given(restTemplate.getForEntity(org.mockito.ArgumentMatchers.anyString(), eq(String.class)))
            .willThrow(new RestClientException("Invalid request"));
        // restTemplate을 호출할 경우 성공 데이터 대신 에러(RestClientException)를 발생시킴

        // when & then
        mockMvc.perform(get("/api/weather")
                .param("lat", "37.5665")
                .param("lon", "126.9780"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("Error fetching weather data."));
        // 컨트롤러 내부에서 restTemplate이 에러 발생시킴
        // 컨트롤러가 이 에러를 잘 처리해서 서버 내부 오류 상태코드(500)와 오류 메시지 응답하는지 검증
    }
}