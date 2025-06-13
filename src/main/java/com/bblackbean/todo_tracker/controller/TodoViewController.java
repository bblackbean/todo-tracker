package com.bblackbean.todo_tracker.controller;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class TodoViewController {
    private final TodoRepository todoRepository;    // final로 선언, 생성자에서만 초기화 가능하며 이후에는 변경되지 않음

    public TodoViewController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    // TodoViewController 클래스가 생성될 때 TodoRepository 객체를 주입받음

    // 조회 화면
    // /view/todos?keyword=Meeting&sortBy=title&order=desc
    @GetMapping("/view/todos")
    public String viewTodos(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            Model model) {

        /**
         * 정렬
         * */
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        // Sort.Direction : Spring Data JPA에서 제공하는 열거형(Enum). 데이터를 정렬할 때 정렬 순서를 지정하는 데 사용됨
        //     Sort.Direction.ASC  : 데이터를 오름차순으로 정렬
        //     Sort.Direction.DESC : 데이터를 내림차순으로 정렬


        /**
         * 페이징
         * */
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        // PageRequest.of(page, size, Sort)
        //     - page : 요청한 페이지 번호
        //     - size : 각 페이지에 포함될 데이터 개수
        //     - Sort : 정렬 기준(Sort.by(direction, sortBy))

        Page<Todo> todoPage;
        // Page<Todo> : Spring Data JPA에서 제공하는 페이징 데이터 객체
        // 페이지에 해당하는 데이터(todoPage.getContent())와 메타정보(전체 페이지 수, 현재 페이지 등)를 포함

        if (keyword != null) {  // 검색어가 있으면..
            todoPage = todoRepository.findByTitleContainingIgnoreCase(keyword, pageable);  // title에 keyword가 포함된 항목 검색(대소문자 구분 없이 검색, sort로 정렬
        } else {
            todoPage = todoRepository.findAll(pageable);
        }
        // 현재 페이지의 데이터만 가져오며, 전체에서 일부 데이터만 반환함
        // SQL
        //     SELECT * FROM todo
        //      WHERE title LIKE '%keyword%'
        //      ORDER BY <sort_by> ASC/DESC
        //      LIMIT <size> OFFSET <page * size>;


        /**
         * 템플릿에 정보 전달
         * */
        model.addAttribute("todoPage", todoPage);                   // 전체 페이징 정보 객체
        model.addAttribute("todos", todoPage.getContent());         // 현재 페이지 데이터
        model.addAttribute("todo", new Todo());                     // 등록 폼용 빈 객체
        model.addAttribute("currentPage", page);                    // 현재 페이지 번호
        model.addAttribute("totalPages", todoPage.getTotalPages()); // 전체 페이지 수
        // todoPage.getContent()    : 현재 페이지에 표시할 실제 데이터(항목들)
        // todoPage.getTotalPages() : 전체 페이지 수

        return "todo-list";     // templates/todo-list.html 렌더링
    }

    // 등록
    @PostMapping("/view/todos")
    public String addTodo(@ModelAttribute Todo todo) {
        todo.setCompleted(false);   // 기본값 : 미완료
        todoRepository.save(todo);
        return "redirect:/view/todos";
    }

    // 삭제
    @PostMapping("/view/todos/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
        return "redirect:/view/todos";
    }

    // 수정 폼 이동
    @GetMapping("/view/todos/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID 없음: " + id));
        model.addAttribute("todo", todo);
        return "edit";
    }

    // 수정
    @PostMapping("/view/todos/edit")
    public String editTodo(@ModelAttribute Todo todo) {
        todoRepository.save(todo);  // 같은 ID면 덮어씀
        return "redirect:/view/todos";
    }

    // 체크박스 토글
    @PostMapping("/view/todos/{id}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleCompleted(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean completed = body.get("completed");

        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setCompleted(completed);
                    todoRepository.save(todo);
                    return ResponseEntity.ok().build();     // 응답내용 없이 200 OK (비동기 요청이라 redirect 필요없음)
                }).orElse(ResponseEntity.notFound().build());
    }

}
