package com.bblackbean.todo_tracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI todoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo Tracker API")
                        .description("할 일 관리 프로젝트 API 문서입니다.")
                        .version("v1.0"));
    }
}
