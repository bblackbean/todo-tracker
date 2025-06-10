package com.bblackbean.todo_tracker.repository;

import com.bblackbean.todo_tracker.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
//    List<Todo> findByTitleContaining(String Keyword);

    List<Todo> findByCompleted(boolean completed);

    @Query("SELECT t FROM Todo t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY t.completed ASC")
    List<Todo> advancedSearch(@Param("keyword") String keyword);
}
