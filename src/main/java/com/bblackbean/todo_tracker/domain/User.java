package com.bblackbean.todo_tracker.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity     // 이 클래스가 JPA 엔티티임을 선언(이 클래스는 데이터베이스 테이블과 매핑됨)
@Table(name = "users")  // "users"라는 테이블과 매핑됨
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    // @Getter, @Setter : getter와 setter 메서드 자동 생성
    // @NoArgsConstructor : 무매개변수 생성자 생성
    // @AllArgsConstructor : 모든 필드를 매개변수로 갖는 생성자를 생성
    // @Builder : Builder 패턴을 사용할 수 있도록 지원하는 Lombok 어노테이션

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // @Id : 이 필드가 Primary Key(기본키)임을 나타냄
    // @GeneratedValue(strategy = GenerationType.IDENTITY) : 데이터베이스에서 기본키를 자동으로 생성함.
    //      여기서 IDENTITY는 주로 MySQL 같은 데이터베이스를 사용할 때, Auto Increment를 이용하여 키를 생서하도록 설정

    @Column(nullable = false, unique = true, length = 50)
    private String username;    // 로그인 아이디
    // @Column : 이 필드가 데이터베이스 테이블의 컬럼임을 정의
    //      nullable = false : 해당 컬럼 값은 null이 될 수 없음
    //      unique = true : 이 컬럼은 중복을 허용하지 않음
    //      length = 50 : 컬럼 값의 최대 길이를 50자로 제한함

    @Column(nullable = false)
    private String password;    // BCrypt로 암호화된 비밀번호

    // 간단히 ROLE_USER 하나만 부여
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;  // 권한목록 (예 : "ROLE_USER")
    // @ElementCollection : 해당 필드가 여러 값을 가질 수 있음을 나타냄. 컬렉션 타입(여기서는 Set<String>)으로 저장됨
    //                      이 필드는 별도의 테이블에 저장되며, 테이블 연관 관계를 정의함
    //      fetch = FetchType.EAGER : roles의 값을 항상 즉시 로딩(Eager Loading)한다. 즉 User 엔티티를 조회하면 roles 값도 즉시 가져옴
    // @CollectionTable : 해당 필드가 저장될 별도의 테이블 정보를 정의함
    //      name = "user_role" : user_role 이라는 테이블에 권한들이 저장됨
    //      joinColumns = @JoinColumn(name = "user_id") : user_role 테이블에서 user_id를 외래키로 사용하여 User 테이블과 연결됨
    // @Column(name = "role") : user_role 테이블에서 각 권한 문자열(예 : "ROLE_USER")이 저장될 컬럼 이름을 "role"로 지정
    //      roles : 사용자의 권한 문자열(예 : "ROLE_USER")을 저장함. Set을 사용하여 중복되지 않는 값을 저장
}
