package com.bblackbean.todo_tracker.service;

import com.bblackbean.todo_tracker.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface TodoService {
    List<Todo> findAll();

    Optional<Todo> findById(Long id);

    Todo save(Todo todo);

    void delete(Long id);

    List<Todo> findByCompleted(boolean completed);

    List<Todo> search(String keyword);

    List<Todo> sortBy(String sortBy, Sort.Direction direction);

    Page<Todo> paging(Pageable pageable);
}
