package com.bblackbean.todo_tracker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
    @Id // 반드시 jakarta.persistence.Id 여야 함
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private boolean completed;

    private String color;

    // 날짜
    private LocalDate startDate;
    private LocalDate endDate;

    public Todo(String title, LocalDate startDate, LocalDate endDate, String color) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
    }
}
