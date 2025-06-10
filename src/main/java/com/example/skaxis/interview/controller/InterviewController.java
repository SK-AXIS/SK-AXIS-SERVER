package com.example.skaxis.interview.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.skaxis.interview.dto.GetInterviewsResponseDto;
import com.example.skaxis.interview.dto.UpdateInterviewRequestDto;
import com.example.skaxis.interview.service.InterviewService;
import com.example.skaxis.interview.dto.CreateInterviewRequestDto;
import com.example.skaxis.interview.dto.GetInterviewByIdResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interviews")
public class InterviewController {
    private final InterviewService interviewService;

    @GetMapping("/")
    public ResponseEntity<?> getAllInterviews() {
        try {
            GetInterviewsResponseDto interviewList = interviewService.getAllInterviews();
            if (interviewList.getInterviewSessions() == null || interviewList.getInterviewSessions().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No interviews found");
            }
            return ResponseEntity.ok(interviewList);
        } catch (Exception e) {
            log.error("Error fetching interviews: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createInterview(@RequestBody CreateInterviewRequestDto createInterviewRequestDto) {
        try {
            if (createInterviewRequestDto == null || createInterviewRequestDto.getRoomNo() == null ||
                createInterviewRequestDto.getRound() <= 0 || createInterviewRequestDto.getScheduledAt() == null ||
                createInterviewRequestDto.getOrderNo() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
            }
            interviewService.createInterview(createInterviewRequestDto);
            return ResponseEntity.ok().body("Interview created successfully");
        } catch (Exception e) {
            log.error("Error creating interview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @DeleteMapping("/{interviewId}")
    public ResponseEntity<?> deleteInterview(@PathVariable Long interviewId) {
        try {
            if (interviewId == null || interviewId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid interview ID");
            }
            interviewService.deleteInterview(interviewId);
            return ResponseEntity.ok().body("Interview deleted successfully"); 
        } catch (Exception e) {
            log.error("Error deleting interview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
    
    @PutMapping("/{interviewId}")
    public ResponseEntity<?> updateInterview(@RequestBody UpdateInterviewRequestDto updateInterviewRequestDto, @PathVariable Long interviewId) {
        try {
            if (interviewId == null || interviewId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid interview ID");
            }
            if (updateInterviewRequestDto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
            }
            interviewService.updateInterview(updateInterviewRequestDto, interviewId);
            return ResponseEntity.ok().body("Interview updated successfully");
        } catch (Exception e) {
            log.error("Error updating interview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/{interviewId}")
    public ResponseEntity<?> getInterviewById(@PathVariable Long interviewId) {
        try {
            if (interviewId == null || interviewId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid interview ID");
            }
            GetInterviewByIdResponseDto interview = interviewService.getInterviewById(interviewId);
            if (interview == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interview not found");
            }
            return ResponseEntity.ok(interview);
        } catch (Exception e) {
            log.error("Error fetching interview by ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "모든 면접 세션 및 관련 데이터 삭제",
        description = "⚠️ 이 API는 전체 면접 세션과 관련된 모든 지원자 연결, 질문, 평가 결과 파일 경로 등을 영구적으로 삭제합니다. 관리자만 사용해야 합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "전체 면접 세션 삭제 성공")
        }
    )
    public ResponseEntity<Void> deleteAllInterviews(@RequestParam(value = "deleteFiles", required = false, defaultValue = "false") boolean deleteFiles) {
        interviewService.deleteAllInterviewsAndRelatedData(deleteFiles);
        return ResponseEntity.noContent().build();
    }
}
