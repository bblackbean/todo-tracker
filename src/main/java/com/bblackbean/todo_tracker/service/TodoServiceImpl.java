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
    public List<TodoResponse> findAll() {
        return todoRepository.findAll().stream().map(TodoMapper::toResponse).toList();
    }

    @Override
    public Optional<TodoResponse> findById(Long id) {
        return todoRepository.findById(id).map(TodoMapper::toResponse);
    }

    @Override
    public TodoResponse save(TodoRequest request) {
        Todo todo = TodoMapper.toEntity(request);

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }

        Todo saved = todoRepository.save(todo);
        return TodoMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        todoRepository.deleteById(id);
    }

    @Override
    public List<TodoResponse> findByCompleted(boolean completed) {
        return todoRepository.findByCompleted(completed).stream().map(TodoMapper::toResponse).toList();
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
        .orElseThrow(() -> new IllegalArgumentException("해당 Id의 할 일을 찾을 수 없습니다: " + id));

        // 요청받은 데이터로 엔티티의 모든 필드를 업데이트
        todo.setTitle(request.getTitle());
        todo.setCompleted(request.isCompleted());

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }
        todo.setStartDate(request.getStartDate());
        todo.setEndDate(request.getEndDate());

        Todo saved = todoRepository.save(todo);
        return TodoMapper.toResponse(saved);
    }
}
