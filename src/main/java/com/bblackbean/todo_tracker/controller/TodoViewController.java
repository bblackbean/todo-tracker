package com.bblackbean.todo_tracker.controller;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.repository.TodoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TodoViewController {
    private final TodoRepository todoRepository;    // final로 선언, 생성자에서만 초기화 가능하며 이후에는 변경되지 않음

    public TodoViewController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    // TodoViewController 클래스가 생성될 때 TodoRepository 객체를 주입받음

    // 조회 화면
    @GetMapping("/view/todos")
    public String viewTodos(Model model) {
        model.addAttribute("todos", todoRepository.findAll());
        model.addAttribute("todo", new Todo()); // 등록 폼용 빈 객체
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
}
