package com.bblackbean.todo_tracker.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

// Redis 카운터 서비스
@Service
@RequiredArgsConstructor
public class LoginAttemptService {
    private final StringRedisTemplate redis;

    private static final int MAX_ATTEMPTS = 5;  // 최대 5번 로그인 시도 가능
    private static final Duration TTL = Duration.ofMinutes(10); // 한 번 틀리기 시작하면 기록 10분간 유지

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
            return Integer.parseInt(v) >= MAX_ATTEMPTS;
        } catch ( NumberFormatException e ) {
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
            redis.expire(k, TTL);
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
