package com.example.skaxis.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Schema(description = "로그인 요청 DTO")
@Data
public class UserLoginRequest {
    @Schema(description = "사용자명", example = "admin")
    private String userName;
    @Schema(description = "비밀번호", example = "password")
    private String password;
}
