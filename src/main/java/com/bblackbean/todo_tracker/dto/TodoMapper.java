package com.bblackbean.todo_tracker.dto;

import com.bblackbean.todo_tracker.domain.Todo;

public class TodoMapper {
    public static Todo toEntity(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setCompleted(false);   // 기본값
        return todo;
    }

    public static TodoResponse toResponse(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.isCompleted());
    }
}
