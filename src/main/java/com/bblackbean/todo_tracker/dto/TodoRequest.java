package com.bblackbean.todo_tracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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

    @NotNull(message = "시작일을 입력하세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "종료일을 입력하세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
