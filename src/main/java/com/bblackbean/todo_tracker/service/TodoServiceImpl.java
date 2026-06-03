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
        validateDateRange(request);
        Todo todo = TodoMapper.toEntity(request);
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
        validateDateRange(request);
        Todo todo = findTodoById(id);

        todo.setTitle(request.getTitle());
        todo.setCompleted(request.isCompleted());
        todo.setStartDate(request.getStartDate());
        todo.setEndDate(request.getEndDate());
        todo.setColor(request.getColor());

        Todo saved = todoRepository.save(todo);
        return TodoMapper.toResponse(saved);
    }

    @Override
    public void toggleCompleted(Long id, boolean completed) {
        Todo todo = findTodoById(id);
        todo.setCompleted(completed);
        todoRepository.save(todo);
    }

    @Override
    public Page<Todo> findPage(String keyword, Pageable pageable) {
        if (keyword != null) {
            return todoRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        }
        return todoRepository.findAll(pageable);
    }

    @Override
    public Todo findTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 Id의 할 일을 찾을 수 없습니다: " + id));
    }

    @Override
    public void saveTodo(Todo todo) {
        todo.setCompleted(false);
        todoRepository.save(todo);
    }

    @Override
    public void updateTodo(Todo todo) {
        todoRepository.save(todo);
    }

    private void validateDateRange(TodoRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }
    }
}
