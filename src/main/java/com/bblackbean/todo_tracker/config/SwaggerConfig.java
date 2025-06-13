package com.bblackbean.todo_tracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  // 스프링 설정 클래스임을 나타냄. 이 클래스 내의 메서드들은 스프링 컨텍스트에서 관리되는 빈으로 등록될 수 있음
public class SwaggerConfig {
    @Bean   // todoOpenAPI 메서드가 반환하는 객체를 빈으로 등록함
    public OpenAPI todoOpenAPI() {
        return new OpenAPI()    // Swagger 구현, Open API 문서 구성
                .info(new Info()
                        .title("Todo Tracker API")  // API 문서의 이름을 설정
                        .description("할 일 관리 프로젝트 API 문서입니다.")  // API 설명 설정
                        .version("v1.0"));  // API 버전 설정
        // swagger-ui.html 경로에서 실행
    }
}
