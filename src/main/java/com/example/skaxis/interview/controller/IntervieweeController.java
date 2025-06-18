package com.example.skaxis.interview.controller;

import com.example.skaxis.interview.dto.interviewee.IntervieweeListResponseDto;
import com.example.skaxis.interview.dto.interviewee.IntervieweeResponseDto;
import com.example.skaxis.interview.service.IntervieweeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interviewees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Interviewee", description = "면접자 관리 API")
public class IntervieweeController {

    private final IntervieweeService intervieweeService;

    @GetMapping
    @Operation(summary = "면접자 목록 조회", description = "모든 면접자 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = IntervieweeListResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<IntervieweeListResponseDto> getInterviewees() {
        IntervieweeListResponseDto response = intervieweeService.getInterviewees();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "면접자 상세 조회", description = "특정 면접자의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = IntervieweeResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "면접자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<IntervieweeResponseDto> getIntervieweeById(
            @Parameter(description = "면접자 ID", example = "1", required = true)
            @PathVariable Long id) {
        IntervieweeResponseDto response = intervieweeService.getIntervieweeById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "면접자 생성", description = "새로운 면접자를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = IntervieweeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<IntervieweeResponseDto> createInterviewee(
            @Parameter(description = "면접자 생성 요청 데이터", required = true)
            @Valid @RequestBody IntervieweeResponseDto request) {
        IntervieweeResponseDto response = intervieweeService.createInterviewee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "면접자 수정", description = "기존 면접자 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = IntervieweeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "면접자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<IntervieweeResponseDto> updateInterviewee(
            @Parameter(description = "면접자 ID", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "면접자 수정 요청 데이터", required = true)
            @Valid @RequestBody IntervieweeResponseDto request) {
        IntervieweeResponseDto response = intervieweeService.updateInterviewee(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "면접자 삭제", description = "특정 면접자를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "면접자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> deleteInterviewee(
            @Parameter(description = "면접자 ID", example = "1", required = true)
            @PathVariable Long id) {
        intervieweeService.deleteInterviewee(id);
        return ResponseEntity.noContent().build();
    }
}