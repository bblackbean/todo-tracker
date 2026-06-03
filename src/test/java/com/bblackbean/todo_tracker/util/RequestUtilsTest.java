package com.bblackbean.todo_tracker.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUtilsTest {

    @Test
    @DisplayName("desc 문자열은 DESC 방향을 반환한다")
    void parseDirection_desc_소문자() {
        assertThat(RequestUtils.parseDirection("desc")).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("DESC 대문자도 DESC 방향을 반환한다")
    void parseDirection_DESC_대문자() {
        assertThat(RequestUtils.parseDirection("DESC")).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("asc 문자열은 ASC 방향을 반환한다")
    void parseDirection_asc() {
        assertThat(RequestUtils.parseDirection("asc")).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("알 수 없는 값은 기본값 ASC를 반환한다")
    void parseDirection_알수없는값_기본ASC() {
        assertThat(RequestUtils.parseDirection("random")).isEqualTo(Sort.Direction.ASC);
        assertThat(RequestUtils.parseDirection("")).isEqualTo(Sort.Direction.ASC);
    }
}
