package com.example.skaxis.auth.constants;

import java.util.List;

public final class AuthConstants {
    private AuthConstants() {
        //인스턴스화 방지 (상수 클래스)
        throw new UnsupportedOperationException("이 클래스는 constants class 이므로 인스턴스화 할 수 없습니다.");
    }
    //토큰 없이 허용된 URI목록
    public static final String[] PERMITTED_URI = {
            "/api/v1/auth/login",
            "/api/v1/auth/signup/**",
            "/api/v1/auth/reissue",
            "/api/v1/uploads/**",  // 미디어 업로드 API 허용
            "/api/v1/interviews/**",
            "/api/v1/interviewees/**",
            "/api/v1/internal/**",
            "/api/v1/weight-config/**",
            // Swagger UI 경로 추가
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };
    //admin user만 접근 가능한 URI
    public static final String[] ADMIN_URI = {
            "/admin/**",
    };
    //허용된 role
    public static final String[] PERMITTED_ROLES = {
            "ADMIN",
            "INTERVIEWER",
    };

    public static final String JWT_ISSUE_HEADER = "Authorization";
    public static final String JWT_RESOLVE_HEADER = "Cookie";
    public static final String ACCESS_PREFIX = "Bearer ";
    public static final String REFRESH_PREFIX = "refresh";

    public static final List<String> PERMITTED_URI_LIST = List.of(PERMITTED_URI);
}
