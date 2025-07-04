package com.bblackbean.todo_tracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration      // 이 클래스는 Spring의 설정 클래스
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    // PasswordEncoder : 비밀번호를 암호화/검증할 때 사용하는 인터페이스

    /**
     * SecurityFilterChain 빈 생성
     * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1) CSRF 설정 : CSRF는 폼로그인/로그아웃 전용 페이지로 제한
            .csrf(csrf -> csrf.disable())
                // CSRF(Cross-Site Request Forgery) 공격 방지를 비활성화 (보통 REST API 사용할 때 비활성화함)

            // 2) URL 접근 권한 설정
            //      특정 URL 패턴에 대한 접근 제어를 설정
            .authorizeHttpRequests(auth -> auth
                // 로그인, 회원가입, 동적 리소스는 모두 허용
                .requestMatchers("/login", "/register",
                    "/css/**", "/js/**", "/images/**",
                    "/swagger-ui.html")
                .permitAll()
                // 그 외 모든 요청은 인증된 사용자만
                .anyRequest().authenticated()   // 그 외 모든 요청은 인증된 사용자만 접근 가능하도록 설정
            )
            // Google OAuth2 로그인
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .defaultSuccessUrl("/view/todos", true)
            )
            // 3) 로그인 폼 설정
            .formLogin(form -> form
                .loginPage("/login")    // 커스텀 로그인 페이지 경로
                .defaultSuccessUrl("/view/todos", true)     // 로그인 성공 시 리다이렉트 (true : 이전 url 경로와 상관없이 항상 이경로로 리다이렉트)
                .permitAll()    // 로그인 폼도 모두 허용 (로그인 자체는 모두 접근할 수 있음)
            )
            // 4) 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/logout")   // 로그아웃 요청 url
                .logoutSuccessUrl("/login?logout")  // 로그아웃 성공 시 리다이렉트 경로
                .invalidateHttpSession(true)        // 세션 무효화
                .deleteCookies("JSESSIONID")    // 쿠키 삭제
            )
            // 5) (선택) 기본 로그인 폼 대신 스프링이 제공하는 기본 제공 페이지 사용
            //.httpBasic(Customizer.withDefaults())
        ;

        return http.build();    // 설정이 완료된 SecurityFilterChain 반환
    }
}
