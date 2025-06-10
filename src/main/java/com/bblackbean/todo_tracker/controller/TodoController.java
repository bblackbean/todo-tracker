package com.bblackbean.todo_tracker.controller;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Todo findById(@PathVariable Long id) {
        return todoRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Todo create(@RequestBody Todo todo) {
        return todoRepository.save(todo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        todoRepository.deleteById(id);
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody Todo updatedTodo) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setTitle(updatedTodo.getTitle());
                    todo.setCompleted(updatedTodo.isCompleted());
                    todoRepository.save(todo);
                    return ResponseEntity.ok(todo);
                }).orElse(ResponseEntity.notFound().build());
    }

    // 검색
//    @GetMapping("/search")
//    public List<Todo> searchByTitle(@RequestParam String keyword) {
//        return todoRepository.findByTitleContaining(keyword);
//    }
//
    // 완료상태에 따라 필터링
    // http://localhost:8080/todos/filter?completed=true
    @GetMapping("/filter")
    public List<Todo> filterByCompleted(@RequestParam boolean completed) {
        return todoRepository.findByCompleted(completed);
    }

    // 검색 2 (키워드 검색 + 정렬)
    // http://localhost:8080/todos/search?keyword=공부
    @GetMapping("/search")
    public List<Todo> searchTodos(@RequestParam String keyword) {
        return todoRepository.advancedSearch(keyword);
    }

    // 정렬
    // http://localhost:8080/todos/sorted?sortBy=completed&order=desc
    @GetMapping("/sorted")
    public List<Todo> getSortedTodos(@RequestParam String sortBy, @RequestParam String order) {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;   // 정렬 방향 결정
        return todoRepository.findAll(Sort.by(direction, sortBy));  // 해당 방향으로 정렬된 결과 반환
    }

    // 페이징
    // http://localhost:8080/todos/paging?page=0&size=2
    @GetMapping("/paging")
    public Page<Todo> getPagedTodos(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        // page : 몇 번째 페이지인지 (0부터 시작)
        // size : 한 페이지당 몇 개 항목을 가져올지
        // Sort.by("id").descending() : 최근에 등록한 할 일부터 보여줌
        return todoRepository.findAll(pageable);
    }
}
