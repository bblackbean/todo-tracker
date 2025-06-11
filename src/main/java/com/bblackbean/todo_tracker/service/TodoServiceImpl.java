package com.bblackbean.todo_tracker.service;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.dto.TodoMapper;
import com.bblackbean.todo_tracker.dto.TodoRequest;
import com.bblackbean.todo_tracker.dto.TodoResponse;
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
    public TodoResponse save(TodoRequest request) {
        Todo todo = TodoMapper.toEntity(request);
        Todo saved = todoRepository.save(todo);
        return TodoMapper.toResponse(saved);
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

    @Override
    public TodoResponse update(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("할 일을 찾을 수 없습니다."));

        todo.setTitle(request.getTitle());
        todo.setCompleted(request.isCompleted());

        Todo saved = todoRepository.save(todo);
        return new TodoResponse(saved.getId(), saved.getTitle(), saved.isCompleted());
    }
}
