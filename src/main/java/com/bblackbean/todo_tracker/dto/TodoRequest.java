package com.bblackbean.todo_tracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor     // 모든 필드 값을 받는 생성자 자동 생성
@NoArgsConstructor      // 기본 생성자 자동 생성
@Getter
@Setter
public class TodoRequest {
    @NotBlank(message="할 일 제목은 필수입니다.")
    @Schema(description = "할 일 제목", example = "공부하기")
    private String title;

    @Schema(description = "완료여부")
    private boolean completed;
}
