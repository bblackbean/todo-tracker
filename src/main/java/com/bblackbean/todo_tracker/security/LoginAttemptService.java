package com.bblackbean.todo_tracker.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

// Redis 카운터 서비스
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptService {
    private final StringRedisTemplate redis;

    @Value("${security.login-attempt.max-attempts}")
    private int maxAttempts;  // 최대 로그인 시도 횟수

    @Value("${security.login-attempt.ttl-minutes}")
    private int ttlMinutes;   // 기록 유지 시간(분)

    private Duration getTtl() {
        return Duration.ofMinutes(ttlMinutes);
    }

    /**
     * 누가 틀렸는지 기록
     * */
    private String key(String id) {
        return "login:fail:" + id;
    }

    /**
     * 차단여부 확인
     * */
    public boolean isBlocked(String id) {
        String v = redis.opsForValue().get(key(id));
        if ( v == null ) return  false;

        try {
            // 5번 이상 틀렸으면 true(차단됨), 아니면 false(통과)
            return Integer.parseInt(v) >= maxAttempts;
        } catch ( NumberFormatException e ) {
            log.warn("Invalid login attempt count found in Redis for id: {}", id, e);
            return false;
        }
    }

    /**
     * 비밀번호 틀렸을 때 기록
     * */
    public int recordFail(String id) {
        String k = key(id);
        Long cnt = redis.opsForValue().increment(k);

        if ( cnt != null && cnt == 1 ) {
            redis.expire(k, getTtl());
        }

        return cnt == null ? 0 : cnt.intValue();
    }

    /**
     * 성공하면 기록 삭제
     * */
    public void recordSuccess(String id) {
        redis.delete(key(id));
    }

    /**
     * 기록이 지워질 때까지 남은 시간을 알려줌
     * */
    public long remainingSeconds(String id) {
        Long sec = redis.getExpire(key(id), TimeUnit.SECONDS);
        return sec == null ? -1 : sec;
    }
}
