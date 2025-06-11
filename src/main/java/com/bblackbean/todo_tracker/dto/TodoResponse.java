package com.bblackbean.todo_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoResponse {
    private Long id;
    private String title;
    private boolean completed;
}
