package org.example.startapi.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc (Swagger UI)를 위한 설정 클래스입니다.
 * 이 클래스를 통해 API 문서의 전반적인 정보를 구성할 수 있습니다.
 */
@Configuration // 이 클래스가 스프링의 설정 파일임을 나타냅니다.
public class OpenApiConfig {

    /**
     * Swagger UI에 표시될 API 문서의 기본 구성을 정의하는 Bean을 생성합니다.
     * @return OpenAPI 객체 (API 문서의 전체적인 설정을 담고 있음)
     */
    @Bean
    public OpenAPI openAPI() {
        // 1. API 문서의 '정보' 부분을 설정합니다. (문서의 표지와 같음)
        Info info = new Info()
                .title("APP API문서")           // API 문서의 메인 제목을 설정합니다.
                .version("v1.0")              // API의 버전을 명시합니다.
                .description("MyApp API 문서")  // API에 대한 간략한 설명을 추가합니다.
                .termsOfService("http://swagger.io/terms/") // 서비스 이용 약관 페이지의 URL을 설정합니다.
                .license(new License().name("Apache 2.0").url("http://springdoc.org")); // API 라이선스 정보를 설정합니다.

        // 2. API 인증 방식을 설정합니다. (JWT Bearer Token 방식)
        // 이 설정을 통해 Swagger UI 우측 상단에 'Authorize' 버튼이 생성됩니다.
        return new OpenAPI()
                .components(new Components()
                        // "bearer-key"라는 이름으로 보안 스킴(Security Scheme)을 정의합니다.
                        // 이 이름은 나중에 컨트롤러에서 @SecurityRequirement 어노테이션을 사용할 때 참조됩니다.
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        // 인증 방식의 타입을 HTTP로 지정합니다.
                                        .type(SecurityScheme.Type.HTTP)
                                        // HTTP 인증 스킴으로 'Bearer' 토큰 방식을 사용한다고 명시합니다.
                                        // "Authorization: Bearer <token>" 형태의 헤더를 의미합니다.
                                        .scheme("bearer")
                                        // Bearer 토큰의 형식이 JWT(JSON Web Token)임을 명시합니다.
                                        .bearerFormat("JWT")
                        )
                )
                // 1번에서 만든 API 정보(info)를 최종 OpenAPI 객체에 설정합니다.
                .info(info);
    }
}
