package com.example.skaxis.weight.controller;

import com.example.skaxis.weight.dto.WeightConfigCreateRequestDto;
import com.example.skaxis.weight.dto.WeightConfigResponseDto;
import com.example.skaxis.weight.service.WeightConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/weight-config")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "가중치 설정 관리", description = "면접 평가 가중치 설정 관리 API")
public class WeightConfigController {
    
    private final WeightConfigService weightConfigService;
    
    /**
     * 현재 활성화된 가중치 설정 조회 (다른 서버에서 호출)
     */
    @GetMapping("/active")
    @Operation(summary = "활성화된 가중치 설정 조회 (현재 FastAPI 호출에선 이것만 사용)", description = "현재 활성화된 가중치 설정을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "활성화된 설정이 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getActiveWeightConfig() {
        try {
            WeightConfigResponseDto activeConfig = weightConfigService.getActiveWeightConfig();
            return ResponseEntity.ok(activeConfig);
        } catch (RuntimeException e) {
            log.error("활성화된 가중치 설정 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "활성화된 가중치 설정이 없습니다"));
        } catch (Exception e) {
            log.error("가중치 설정 조회 중 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다"));
        }
    }
    
    /**
     * 모든 가중치 설정 조회 (관리자용)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "모든 가중치 설정 조회", description = "모든 가중치 설정을 조회합니다 (관리자 권한 필요)")
    public ResponseEntity<?> getAllWeightConfigs() {
        try {
            List<WeightConfigResponseDto> configs = weightConfigService.getAllWeightConfigs();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            log.error("가중치 설정 목록 조회 중 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다"));
        }
    }
    
    /**
     * 새로운 가중치 설정 생성 (관리자용)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "가중치 설정 생성", description = "새로운 가중치 설정을 생성합니다 (관리자 권한 필요)")
    public ResponseEntity<?> createWeightConfig(@Valid @RequestBody WeightConfigCreateRequestDto requestDto) {
        try {
            if (!requestDto.isValidWeightSum()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "가중치의 합은 100이어야 합니다"));
            }
            
            WeightConfigResponseDto createdConfig = weightConfigService.createWeightConfig(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdConfig);
        } catch (IllegalArgumentException e) {
            log.error("가중치 설정 생성 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("가중치 설정 생성 중 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다"));
        }
    }
    
    /**
     * 기본 가중치 설정 초기화 (관리자용)
     */
    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "기본 가중치 설정 초기화", description = "기본 가중치 설정을 초기화합니다 (관리자 권한 필요)")
    public ResponseEntity<?> initializeDefaultWeightConfig() {
        try {
            WeightConfigResponseDto defaultConfig = weightConfigService.initializeDefaultWeightConfig();
            return ResponseEntity.ok(defaultConfig);
        } catch (Exception e) {
            log.error("기본 가중치 설정 초기화 중 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다"));
        }
    }
}