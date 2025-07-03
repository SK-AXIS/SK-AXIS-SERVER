package com.example.skaxis.interview.controller;

import com.example.skaxis.interview.dto.InterviewScheduleResponseDto;
import com.example.skaxis.interview.dto.SimpleInterviewScheduleResponseDto;
import com.example.skaxis.interview.dto.interviewee.IntervieweeListResponseDto;
import com.example.skaxis.interview.dto.interviewee.UpdateIntervieweeRequestDto;
import com.example.skaxis.interview.service.IntervieweeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/interviewees")
@RequiredArgsConstructor
@Tag(name = "면접 대상자 관리", description = "면접 대상자 정보 및 일정 관리 API")
public class IntervieweeController {

    private final IntervieweeService intervieweeService;

    @GetMapping("/simple")
    @Operation(summary = "면접 대상자 목록 조회", description = "필터 조건에 따라 면접 대상자 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = IntervieweeListResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<IntervieweeListResponseDto> getInterviewees(
            @Parameter(description = "면접 날짜 (YYYY-MM-DD)", example = "") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "면접 상태", example = "") @RequestParam(required = false) String status,
            @Parameter(description = "직무", example = "") @RequestParam(required = false) String position) {

        IntervieweeListResponseDto response = intervieweeService.getInterviewees(date, status, position);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{intervieweeId}")
    @Operation(summary = "면접 대상자 정보 및 일정 수정", description = "특정 면접 대상자의 이름, 점수 또는 면접 일정을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "면접 대상자를 찾을 수 없음")
    })
    public ResponseEntity<?> updateInterviewee(
            @Parameter(description = "면접 대상자 ID", required = true) @PathVariable Long intervieweeId,
            @RequestBody UpdateIntervieweeRequestDto requestDto) {
        try {
            // 면접 일정 수정 시 필수 필드 검증
            if (requestDto.getInterviewId() != null) {
                if (requestDto.getStartAt() == null || requestDto.getEndAt() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "면접 일정 수정 시 시작 시간과 종료 시간이 모두 필요합니다."));
                }
            }
            
            intervieweeService.updateInterviewee(intervieweeId, requestDto);
            return ResponseEntity.ok().body(Map.of("message", "면접 대상자 정보가 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            log.error("Error updating interviewee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Internal Server Error"));
        }
    }

    @GetMapping("/interviews")
    @Operation(summary = "날짜별 면접 일정 조회", description = "특정 날짜의 면접 일정 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = SimpleInterviewScheduleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<SimpleInterviewScheduleResponseDto> getInterviewSchedule(
            @Parameter(description = "면접 날짜 (YYYY-MM-DD)", example = "2024-01-15", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        SimpleInterviewScheduleResponseDto response = intervieweeService.getSimpleInterviewScheduleByDate(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/interviews/all")
    @Operation(summary = "전체 면접 일정 조회", description = "모든 날짜의 면접 일정 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = SimpleInterviewScheduleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<SimpleInterviewScheduleResponseDto> getAllInterviewSchedules(
            @Parameter(description = "면접 상태별 필터 (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)", required = false) @RequestParam(required = false) String status) {
        System.out.println("조회 필터 status = " + status);
        SimpleInterviewScheduleResponseDto response = intervieweeService.getAllSimpleInterviewSchedules(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/interviews/detailed")
    @Operation(summary = "상세한 날짜별 면접 일정 조회", description = "특정 날짜의 면접 일정 정보를 상세한 형식으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = InterviewScheduleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<InterviewScheduleResponseDto> getDetailedInterviewSchedule(
            @Parameter(description = "면접 날짜 (YYYY-MM-DD)", example = "2024-01-15", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        InterviewScheduleResponseDto response = intervieweeService.getInterviewScheduleByDate(date);
        return ResponseEntity.ok(response);
    }
}