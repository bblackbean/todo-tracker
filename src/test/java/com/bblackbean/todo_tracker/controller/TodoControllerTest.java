package com.bblackbean.todo_tracker.controller;

import com.bblackbean.todo_tracker.common.ApiResponse;
import com.bblackbean.todo_tracker.dto.TodoRequest;
import com.bblackbean.todo_tracker.dto.TodoResponse;
import com.bblackbean.todo_tracker.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = TodoController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class
        }
)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @Test
    @DisplayName("할 일 전체 조회")
    void getAllTodos() throws Exception {
        TodoResponse response = new TodoResponse(1L, "테스트 할 일", false,
                LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 7), "#000000");
        when(todoService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title", is("테스트 할 일")));
    }

    @Test
    @DisplayName("할 일 등록")
    void createTodo() throws Exception {
        LocalDate start = LocalDate.of(2025, 12, 1);
        LocalDate end = LocalDate.of(2025, 12, 7);
        TodoResponse response = new TodoResponse(1L, "새로운 할 일", false, start, end, "#000000");
        when(todoService.save(any())).thenReturn(response);

        String requestBody = """
                {
                    "title": "새로운 할 일",
                    "startDate": "2025-12-01",
                    "endDate": "2025-12-07"
                }
                """;

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title", is("새로운 할 일")))
                .andExpect(jsonPath("$.data.completed", is(false)));
    }

    @Test
    @DisplayName("할 일 등록 - 필수 날짜 누락 시 400 반환")
    void createTodo_날짜없음_400() throws Exception {
        String requestBody = """
                {
                    "title": "날짜 없는 할 일"
                }
                """;

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("단건 조회 - 존재하는 ID면 200 반환")
    void findById_성공() throws Exception {
        TodoResponse response = new TodoResponse(1L, "할 일", false,
                LocalDate.now(), LocalDate.now().plusDays(1), "#000000");
        when(todoService.findById(1L)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    @DisplayName("단건 조회 - 없는 ID면 404 반환")
    void findById_없는ID_404() throws Exception {
        when(todoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("/todos/popup/{id} 엔드포인트는 제거되어 404 반환")
    void popup_엔드포인트_제거됨() throws Exception {
        mockMvc.perform(get("/todos/popup/1"))
                .andExpect(status().isNotFound());
    }
}
