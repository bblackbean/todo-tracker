package com.bblackbean.todo_tracker.service;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.dto.TodoRequest;
import com.bblackbean.todo_tracker.dto.TodoResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface TodoService {
    List<TodoResponse> findAll();

    Optional<TodoResponse> findById(Long id);

    TodoResponse save(TodoRequest request);

    void delete(Long id);

    List<TodoResponse> findByCompleted(boolean completed);

    List<Todo> search(String keyword);

    List<Todo> sortBy(String sortBy, Sort.Direction direction);

    Page<Todo> paging(Pageable pageable);

    TodoResponse update(Long id, @Valid TodoRequest request);
}
