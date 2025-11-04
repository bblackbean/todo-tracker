package com.bblackbean.todo_tracker.dto;

import com.bblackbean.todo_tracker.domain.Todo;

public class TodoMapper {
    public static Todo toEntity(TodoRequest request) {
        // request의 모든 필드를 사용하는 Todo 생성자로 엔티티 생성
        return new Todo(request.getTitle(), request.getStartDate(), request.getEndDate());
    }

    public static TodoResponse toResponse(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted(), todo.getStartDate(), todo.getEndDate());
    }
}
