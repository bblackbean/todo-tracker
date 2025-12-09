package com.bblackbean.todo_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 클라이언트에 응답을 보낼 DTO
 * */
@Getter
@AllArgsConstructor
public class TodoResponse {
    private Long id;
    private String title;
    private boolean completed;
    private LocalDate startDate;
    private LocalDate endDate;
    private String color;
}
