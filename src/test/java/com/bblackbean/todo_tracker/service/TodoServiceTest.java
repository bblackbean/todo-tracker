package com.bblackbean.todo_tracker.service;

import com.bblackbean.todo_tracker.domain.Todo;
import com.bblackbean.todo_tracker.dto.TodoRequest;
import com.bblackbean.todo_tracker.dto.TodoResponse;
import com.bblackbean.todo_tracker.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class TodoServiceTest {

    @Mock           // 실제 데이터베이스와 상호작용하지 않고, 테스트가 독립적으로 동작할 수 있도록 설정
    private TodoRepository todoRepository;

    @InjectMocks    // TodoServiceImpl 객체에 @Mock으로 생성된 TodoRepository 주입
    private TodoServiceImpl todoService;

    /**
     * TodoServiceImpl.save() 메서드가 새로운 할 일(Todo)을 올바르게 저장하고 적절한 응답을 반환하는지 확인
     * */
    @Test
    @DisplayName("할 일 등록")
    void todoCreate() {
        // given
        /*TodoRequest request = new TodoRequest("테스트 할 일", false);    // TodoRequest 객체로 클라이언트로부터 요청이 전달되었다는 가정을 설정
        Todo savedEntity = new Todo(1L, "테스트 할 일", false); */     // Todo 엔티티(saveEntity)는 데이터베이스에 저장된 할 일의 상태를 가정

//        when(todoRepository.save(any())).thenReturn(savedEntity);
        // when-thenReturn : Mockito에서 사용되는 메서드로, 특정 조건이 만족될 때(Mock 객체에서) 반환값을 지정한다.
        // todoRepository.save() 호출 시 savedEntity를 반환하도록 Mock 설정
        // 이렇게 하면 실제 DB와 상호작용할 필요 없이 가상의 데이터를 반환함

        // when
//        TodoResponse result = todoService.save(request);
        // save() 메서드를 호출하여 결과를 반환받음
        // 이 메서드 내부에서 odoMapper.toEntity()와 todoRepository.save()가 호출됨

        // then
        /*assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("테스트 할 일");
        assertThat(result.isCompleted()).isFalse();*/
        // 반환된 결과(TodoResponse)의 필드 값들이 예상한 값과 일치하는지 검증
    }

    /**
     * TodoServiceImpl.findById() 메서드가 특정 Id를 가진 할 일(Todo)을 올바르게 조회하는지 확인함
     * */
    @Test
    @DisplayName("할 일 단건 조회")
    void todoList() {
        // given
        /*Todo todo = new Todo(1L, "단건 조회", false);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));*/
        // Id가 1인 Todo 엔티티를 데이터베이스에서 조회했을 때 반환될 값을 Mock으로 설정

        // when
        /*Optional<Todo> result = todoService.findById(1L);*/
        // findById() 메서드를 호출하여 특정 Id의 할 일을 찾는 동작 수행

        // then
        /*assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("단건 조회");*/
        // 반환된 결과가 비어 있지 않고(isPresent()), 제목이 예상한 값("단건 조회")과 일치하는지 확인
    }
}
