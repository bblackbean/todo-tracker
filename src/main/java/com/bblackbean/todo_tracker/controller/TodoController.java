package com.bblackbean.todo_tracker.controller;

import com.bblackbean.todo_tracker.common.ApiResponse;
import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.dto.TodoRequest;
import com.bblackbean.todo_tracker.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    // 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<Todo>>> findAll() {
        // ResponseEntity<ApiResponse<List<Todo>>>
        //     ResponseEntity<T> : HTTP 응답 자체를 감싸는 타입. 상태 코드(status)와 헤더, 바디를 가질 수 있음
        //     여기서 바디 <T>는 ApiResponse<List<Todo>>
        // ApiResponse<List<Todo>>
        //     내가 만든 응답 포맷. 제네릭 T에 실제 담을 데이터를 지정하는데, 여기서는 List<Todo>>

        return ResponseEntity.ok(ApiResponse.success(todoService.findAll()));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Todo>> findById(@PathVariable Long id) {
        return todoService.findById(id)
                .map(todo -> ResponseEntity.ok(ApiResponse.success(todo)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("해당 ID의 할 일을 찾을 수 없습니다.")));

        // todoService.findById(id) 은 Optional<Todo> 를 반환
        //     찾으면 Optional.of(todo)
        //     없으면 Optional.empty()
        // map()
        //     Optional 에 값이 있으면 그 값(todo)을 가지고 성공 응답(200 OK + ApiResponse.success(todo))을 만든 뒤 리턴
        // orElse()
        //     Optional.empty() 면 여기로 넘어와, HTTP 상태 404 Not Found, 바디에는 ApiResponse.fail("해당 ID의 할 일을 찾을 수 없습니다.") 을 담아 리턴
    }

    // 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Todo>> create(@Valid @RequestBody TodoRequest request) {   // @Valid : 입력값 검증 수행 / @RequestBody TodoRequest : 요청 JSON을 DTO로 매핑
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setCompleted(false);   // 기본값 설정

        return ResponseEntity.ok(ApiResponse.success("할 일이 등록되었습니다.", todoService.save(todo)));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Todo>> update(@PathVariable Long id, @RequestBody Todo updatedTodo) {
        return todoService.findById(id)
                .map(todo -> {
                    todo.setTitle(updatedTodo.getTitle());
                    todo.setCompleted(updatedTodo.isCompleted());
                    return ResponseEntity.ok(ApiResponse.success("수정 완료", todoService.save(todo)));
                }).orElse(ResponseEntity.notFound().build());
    }

    // 완료상태에 따라 필터링
    // http://localhost:8080/todos/filter?completed=true
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Todo>>> filterByCompleted(@RequestParam boolean completed) {
        return ResponseEntity.ok(ApiResponse.success(todoService.findByCompleted(completed)));
    }

    // 검색 2 (키워드 검색 + 정렬)
    // http://localhost:8080/todos/search?keyword=공부
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Todo>>> searchTodos(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(todoService.search(keyword)));
    }

    // 정렬
    // http://localhost:8080/todos/sorted?sortBy=completed&order=desc
    @GetMapping("/sorted")
    public ResponseEntity<ApiResponse<List<Todo>>> getSortedTodos(@RequestParam String sortBy, @RequestParam String order) {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;   // 정렬 방향 결정
        return ResponseEntity.ok(ApiResponse.success(todoService.sortBy(sortBy, direction)));  // 해당 방향으로 정렬된 결과 반환
    }

    // 페이징
    // http://localhost:8080/todos/paging?page=0&size=2
    @GetMapping("/paging")
    public ResponseEntity<ApiResponse<Page<Todo>>> getPagedTodos(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        // page : 몇 번째 페이지인지 (0부터 시작)
        // size : 한 페이지당 몇 개 항목을 가져올지
        // Sort.by("id").descending() : 최근에 등록한 할 일부터 보여줌
        return ResponseEntity.ok(ApiResponse.success(todoService.paging(pageable)));
    }
}
