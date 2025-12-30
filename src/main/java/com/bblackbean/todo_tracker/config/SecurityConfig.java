package com.bblackbean.todo_tracker.config;

import com.bblackbean.todo_tracker.security.LoginAttemptService;
import com.bblackbean.todo_tracker.security.LoginRateLimitFilter;
import com.bblackbean.todo_tracker.util.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration      // 이 클래스는 Spring의 설정 클래스
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    // PasswordEncoder : 비밀번호를 암호화/검증할 때 사용하는 인터페이스
    private final LoginAttemptService loginAttemptService;
    // RedisConfig에서 생성한 빈을 주입받음
    private final SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry;

    @Bean
    public LoginRateLimitFilter loginRateLimitFilter() {
        return new LoginRateLimitFilter(loginAttemptService);
    }

    /**
     * SecurityFilterChain 빈 생성
     * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1) CSRF 설정 : 보안을 위해 활성화
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )

            // 2) URL 접근 권한 설정
            //      특정 URL 패턴에 대한 접근 제어를 설정
            .authorizeHttpRequests(auth -> auth
                // 로그인, 회원가입, 동적 리소스는 모두 허용
                .requestMatchers("/login", "/register",
                    "/css/**", "/js/**", "/images/**",
                    "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**") // Swagger 관련 리소스 경로 추가
                .permitAll()
                // 그 외 모든 요청은 인증된 사용자만
                .anyRequest().authenticated()   // 그 외 모든 요청은 인증된 사용자만 접근 가능하도록 설정
            )
            // 로그인 시도 선차단 필터 추가
            .addFilterBefore(loginRateLimitFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            // Google OAuth2 로그인
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .defaultSuccessUrl("/view/todos", true)
            )
            // 3) 로그인 폼 설정
            .formLogin(form -> form
                .loginPage("/login")    // 커스텀 로그인 페이지 경로
                .defaultSuccessUrl("/view/todos", true)     // 로그인 성공 시 리다이렉트 (true : 이전 url 경로와 상관없이 항상 이경로로 리다이렉트)
                // 실패하면 카운트 증가
                .failureHandler((req, res, ex) -> {
                    String ip = RequestUtils.getClientIp(req);
                    int cnt = loginAttemptService.recordFail(ip);
                    res.sendRedirect("/login?error=true&attempts=" + cnt);
                })
                // 성공하면 카운트 삭제
                .successHandler((req, res, auth2) -> {
                    String ip = RequestUtils.getClientIp(req);
                    loginAttemptService.recordSuccess(ip);
                    res.sendRedirect("/view/todos");
                })
                .permitAll()    // 로그인 폼도 모두 허용 (로그인 자체는 모두 접근할 수 있음)
            )
            // 4) 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/logout")   // 로그아웃 요청 url
                .logoutSuccessUrl("/login?logout")  // 로그아웃 성공 시 리다이렉트 경로
                .invalidateHttpSession(true)        // 세션 무효화
                .deleteCookies("JSESSIONID")    // 쿠키 삭제
            )
            // 5) 세션 관리 (중복 로그인 방지)
            .sessionManagement(session -> session
                .maximumSessions(1)               // 사용자당 최대 세션 수 (1개로 제한)
                .maxSessionsPreventsLogin(false)  // false: 새로운 로그인 허용 (기존 세션 만료), true: 새로운 로그인 차단
                .sessionRegistry(sessionRegistry) // Redis 기반 세션 레지스트리 연결
            )
            // 5) (선택) 기본 로그인 폼 대신 스프링이 제공하는 기본 제공 페이지 사용
            //.httpBasic(Customizer.withDefaults())
        ;

        return http.build();    // 설정이 완료된 SecurityFilterChain 반환
    }
}
