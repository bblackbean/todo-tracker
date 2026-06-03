package com.bblackbean.todo_tracker.service;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.dto.TodoRequest;
import com.bblackbean.todo_tracker.dto.TodoResponse;
import com.bblackbean.todo_tracker.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    // ── save ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("할 일 등록 - 종료일이 시작일보다 빠르면 예외 발생")
    void save_날짜검증_실패() {
        TodoRequest request = new TodoRequest(
                "테스트", false,
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 1),
                "#000000"
        );

        assertThatThrownBy(() -> todoService.save(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종료일은 시작일보다 빠를 수 없습니다.");
        verify(todoRepository, never()).save(any());
    }

    @Test
    @DisplayName("할 일 등록 - 유효한 요청이면 저장 후 응답 반환")
    void save_성공() {
        LocalDate start = LocalDate.of(2025, 12, 1);
        LocalDate end = LocalDate.of(2025, 12, 7);
        TodoRequest request = new TodoRequest("공부하기", false, start, end, "#ff0000");
        Todo saved = new Todo(1L, "공부하기", false, "#ff0000", start, end);
        when(todoRepository.save(any())).thenReturn(saved);

        TodoResponse result = todoService.save(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("공부하기");
        assertThat(result.isCompleted()).isFalse();
    }

    // ── update ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("할 일 수정 - 종료일이 시작일보다 빠르면 예외 발생")
    void update_날짜검증_실패() {
        TodoRequest request = new TodoRequest(
                "수정", false,
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 1),
                "#000000"
        );

        assertThatThrownBy(() -> todoService.update(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("종료일은 시작일보다 빠를 수 없습니다.");
        verify(todoRepository, never()).findById(any());
    }

    // ── findTodoById ──────────────────────────────────────────────────────

    @Test
    @DisplayName("findTodoById - 존재하지 않는 ID면 예외 발생")
    void findTodoById_없는ID_예외() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.findTodoById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findTodoById - 존재하는 ID면 엔티티 반환")
    void findTodoById_성공() {
        Todo todo = new Todo(1L, "할 일", false, "#000000", LocalDate.now(), LocalDate.now().plusDays(1));
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        Todo result = todoService.findTodoById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("할 일");
    }

    // ── toggleCompleted ───────────────────────────────────────────────────

    @Test
    @DisplayName("toggleCompleted - 완료 상태 변경 후 저장")
    void toggleCompleted_완료처리() {
        Todo todo = new Todo(1L, "할 일", false, "#000000", LocalDate.now(), LocalDate.now().plusDays(1));
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        todoService.toggleCompleted(1L, true);

        assertThat(todo.isCompleted()).isTrue();
        verify(todoRepository).save(todo);
    }

    @Test
    @DisplayName("toggleCompleted - 존재하지 않는 ID면 예외 발생")
    void toggleCompleted_없는ID_예외() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.toggleCompleted(99L, true))
                .isInstanceOf(IllegalArgumentException.class);
        verify(todoRepository, never()).save(any());
    }

    // ── findPage ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findPage - 키워드 없으면 전체 페이징 조회")
    void findPage_키워드없음() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Todo> mockPage = new PageImpl<>(List.of());
        when(todoRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Todo> result = todoService.findPage(null, pageable);

        verify(todoRepository).findAll(pageable);
        verify(todoRepository, never()).findByTitleContainingIgnoreCase(any(), any());
        assertThat(result).isSameAs(mockPage);
    }

    @Test
    @DisplayName("findPage - 키워드 있으면 키워드 검색 조회")
    void findPage_키워드있음() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Todo> mockPage = new PageImpl<>(List.of());
        when(todoRepository.findByTitleContainingIgnoreCase("공부", pageable)).thenReturn(mockPage);

        Page<Todo> result = todoService.findPage("공부", pageable);

        verify(todoRepository).findByTitleContainingIgnoreCase("공부", pageable);
        verify(todoRepository, never()).findAll(pageable);
        assertThat(result).isSameAs(mockPage);
    }

    // ── saveTodo ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveTodo - completed를 항상 false로 설정 후 저장")
    void saveTodo_기본완료상태_미완료() {
        Todo todo = new Todo();
        todo.setCompleted(true); // 외부에서 true로 넣어도
        when(todoRepository.save(any())).thenReturn(todo);

        todoService.saveTodo(todo);

        assertThat(todo.isCompleted()).isFalse(); // 서비스가 false로 강제
        verify(todoRepository).save(todo);
    }
}
