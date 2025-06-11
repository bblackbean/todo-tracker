package com.bblackbean.todo_tracker.service;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    @Override
    public Todo save(Todo todo) {
        return todoRepository.save(todo);
    }

    @Override
    public void delete(Long id) {
        todoRepository.deleteById(id);
    }

    @Override
    public List<Todo> findByCompleted(boolean completed) {
        return todoRepository.findByCompleted(completed);
    }

    @Override
    public List<Todo> search(String keyword) {
        return todoRepository.advancedSearch(keyword);
    }

    @Override
    public List<Todo> sortBy(String sortBy, Sort.Direction direction) {
        return todoRepository.findAll(Sort.by(direction, sortBy));
    }

    @Override
    public Page<Todo> paging(Pageable pageable) {
        return todoRepository.findAll(pageable);
    }
}
