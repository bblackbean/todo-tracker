package com.bblackbean.todo_tracker.controller;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.service.TodoService;
import com.bblackbean.todo_tracker.util.RequestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class TodoViewController {

    private final TodoService todoService;

    public TodoViewController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/view/todos")
    public String viewTodos(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            Model model) {

        Sort.Direction direction = RequestUtils.parseDirection(order);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Todo> todoPage = todoService.findPage(keyword, pageable);

        model.addAttribute("todoPage", todoPage);
        model.addAttribute("todos", todoPage.getContent());
        model.addAttribute("todo", new Todo());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", todoPage.getTotalPages());

        return "todo-list";
    }

    @PostMapping("/view/todos")
    public String addTodo(@ModelAttribute Todo todo) {
        todoService.saveTodo(todo);
        return "redirect:/view/todos";
    }

    @PostMapping("/view/todos/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        todoService.delete(id);
        return "redirect:/view/todos";
    }

    @GetMapping("/view/todos/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("todo", todoService.findTodoById(id));
        return "edit";
    }

    @PostMapping("/view/todos/edit")
    public String editTodo(@ModelAttribute Todo todo) {
        todoService.updateTodo(todo);
        return "redirect:/view/todos";
    }

    @PostMapping("/view/todos/{id}/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleCompleted(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        todoService.toggleCompleted(id, body.get("completed"));
        return ResponseEntity.ok().build();
    }
}
