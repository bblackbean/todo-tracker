package com.bblackbean.todo_tracker.repository;

import com.bblackbean.todo_tracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JPA를 활용하여 User 엔티티에 대한 데이터 접근 로직을 관리
public interface UserRepository extends JpaRepository<User, Long> {
    // * JpaRepository<User, Long>
    // Spring Data JPA에서 제공하는 JpaRepository를 상속받는 인터페이스
    // 이 상속을 통해 별도로 구현하지 않아도 User 엔티티에 대한 다양한 데이터베이스 연산을 사용 가능
    // * 제네릭 타입 설명
    //   - User : 관리할 엔티티 클래스. 여기서는 User 클래스와 연관된 데이터베이스 테이블에 대한 작업을 수행
    //   - Long : 엔티티의 기본키(PK) 타입. User 클래스의 id 필드가 Long 타입이므로 이렇게 지정

    // * JpaRepository가 기본적으로 제공하는 주요 메서드들
    //   - save() : 데이터를 저장하거나 업데이트함
    //   - findById() : 기본 키를 통해 데이터를 조회
    //   - findAll() : 모든 데이터를 조회
    //   - deleteById() : 기본 키를 통해 데이터를 삭제
    //   - count() : 데이터 개수를 반환

    Optional<User> findByUsername(String username);
    // SELECT * FROM users WHERE username = :username;
}
