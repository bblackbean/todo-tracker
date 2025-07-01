package com.bblackbean.todo_tracker.service;

import com.bblackbean.todo_tracker.domain.User;
import com.bblackbean.todo_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor    // final 키워드가 붙은 필드에 대해 자동으로 생성자 생성
public class CustomUserDetailService implements UserDetailsService {
    // implements UserDetailsService : Spring Security가 제공하는 UserDetailsService 인터페이스를 구현
    //                                 이 인터페이스는 사용자 정보를 로드하는 핵심 메서드인 loadUserByUsername을 제공함

    private final UserRepository userRepository;
    // 데이터베이스에서 사용자 정보를 로드하기 위해 사용되는 JPA 레포지토리
    // 이 필드는 final로 선언되어 불변성을 유지하며, 생성자 주입을 통해 전달받음

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1) 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        // userRepository.findByUsername(username) : UserRepository에서 username을 기준으로 사용자 정보를 조회
        // orElseThrow : Optional로 반환된 결과값이 없는 경우 예외를 던짐

        // 2) User 객체의 roles(Set<String>)을 SimpleGrantedAuthority 리스트로 변환
        var authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // user.getRoles() : Set<String> 형태로 저장된 사용자 권한 목록을 반환. ["ROLE_USER", "ROLE_ADMIN"]과 같은 값을 가질 수 있음
        // .stream() : 자바 스트림(Stream) API를 활용하여 Set 데이터를 처리
        // SimpleGrantedAuthority : Spring Security에서 정의된 권한 클래스. 문자열 권한값(예 : "ROLE_USER")을 인스턴스로 변환
        // .collect(Collectors.toList()) : Stream 처리가 완료된 데이터를 List 형태로 수집
        // 이렇게 변환된 리스트는 Spring Security에서 사용되는 권한 데이터로 활용됨

        // 3) Spring Security가 사용하는 UserDetail로 반환
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        // org.springframework.security.core.userdetails.User : Spring Security에서 제공하는 UserDetails의 기본 구현체
    }
}
