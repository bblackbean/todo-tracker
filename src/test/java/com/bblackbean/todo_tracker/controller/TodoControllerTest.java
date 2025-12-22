package com.bblackbean.todo_tracker.controller;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest         // 스프링 부트 애플리케이션을 테스트 환경에서 실행
@AutoConfigureMockMvc   // MockMvc(실제 서버를 실행하지 않고, 컨트롤러의 동작으로 가짜 요청으로 테스트할 수 있도록 도와줌)를 자동으로 구성
public class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;    // MockMvc : 컨트롤러를 테스트할 때 HTTP 요청을 시뮬레이션하고, 그 응답을 검증할 수 있는 도구

    @Autowired
    private TodoRepository todoRepository;

    @Test   // 테스트 메서드로 실행될 수 있도록 지정
    @DisplayName("할 일 전체 조회")   // 테스트 설명 추가
    void getAllTodos() throws Exception {
        todoRepository.save(new Todo("테스트 할 일", LocalDate.parse("2025-12-01"), LocalDate.parse("2025-12-07"), "#000000"));  // Todo 저장

        mockMvc.perform(get("/todos"))  // /todos 라는 url로 GET 요청을 보냄
                .andExpect(status().isOk())        // 결과 검증 - http 응답 상태 코드가 200(요청 성공)인지 확인
                .andExpect(jsonPath("$", not(empty())))     // 응답 데이터 확인
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title", is("테스트 할 일")));

        // jsonPath : JSON 데이터의 특정 값을 추출하거나 검증할 수 있는 라이브러리
        //   $ : JSON의 전체 데이터를 나타냄
        //   not(empty()) : 응답 데이터가 비어있지 않은지 확인
        //   data : JSON 응답 데이터의 data 키
        //   $.data[0].title : data 배열의 첫 번째 객쳉의 title 속성 값을 가져와 "테스트 할 일"과 같은지 확인
        // 목록을 전체 조회 하기 때문에 data는 배열임
    }

    @Test
    @DisplayName("할 일 등록")
    void createTodo() throws Exception {
        String requestBody = "{\n"
            + "    \"title\": \"새로운 할 일\"\n"
            + "}"
        ;   // 요청 데이터를 JSON 형식으로 정의

        mockMvc.perform(post("/todos")
                    .contentType(MediaType.APPLICATION_JSON)    // 요청 데이터 형식 지정 : JSON
                    .content(requestBody))                      // 요청 데이터 본문(requestBody)을 포함
                .andExpect(status().isOk())                     // 상태 코드가 200인지 확인
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title", is("새로운 할 일")))     // 응답 데이터의 title 확인
                .andExpect(jsonPath("$.data.completed", is(false)));       // 응답 데이터의 completed 확인

        // createTodo의 data는 객체라서 인덱싱 안함
    }

}
