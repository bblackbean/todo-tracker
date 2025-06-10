package com.bblackbean.todo_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoRequest {
    @NotBlank(message="할 일 제목은 필수입니다.")
    private String title;
}
