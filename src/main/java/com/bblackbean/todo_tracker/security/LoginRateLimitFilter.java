package com.bblackbean.todo_tracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginAttemptService attemptService;

    // 로그인 POST 전에 차단여부를 먼저 막음
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        // formLogin POST 요청만 선차단
        // /login으로 POST 요청 들어오면 요청 보낸 IP를 키로 Redis에 저장된 실패 횟수를 확인, 차단이면 리다이렉트로 로그인 페이지로 돌려보냄
        if ("/login".equals(req.getServletPath()) && "POST".equalsIgnoreCase(req.getMethod()) ) {
            String ip = req.getRemoteAddr();

            if ( attemptService.isBlocked(ip) ) {
                long remain = attemptService.remainingSeconds(ip);

                // 화면 로그인은 json보다 리다이렉트가 더 자연스러움
                res.sendRedirect("/login?blocked=true&retryAfterSec=" + remain);
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
