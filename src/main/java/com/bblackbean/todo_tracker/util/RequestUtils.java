package com.bblackbean.todo_tracker.util;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestUtils {
    private RequestUtils() {}   // 인스턴스화 방지

    /**
     * http 요청을 보낸 클라이언트의 실제 IP 주소 추출
     * */
    public static String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        // X-Forwarded-For : http 요청이 프록시나 로드 밸런서를 통과할 때, 원래 클라이언트의 IP 주소를 보존하기 위한 표준 헤더

        // 헤더가 없거나, 비어있거나, unknown으로 설정된 경우 체크(일부 프록시는 클라이언트 IP를 알 수 없을 때 unknown 값 설정
        if ( xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader) ) {
            return request.getRemoteAddr(); // 조건에 해당할 경우 직접 연결된 클라이언트의 IP 반환
        }

        // "client, proxy1, proxy2" 형태일 수 있으니 첫 번째만
        return xfHeader.split(",")[0].trim();
    }
}
