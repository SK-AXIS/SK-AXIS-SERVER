package com.example.skaxis.interview.controller;

import com.example.skaxis.interview.dto.*;
import com.example.skaxis.interview.dto.interviewee.IntervieweeListResponseDto;
import com.example.skaxis.interview.model.Interview;
import com.example.skaxis.interview.model.InterviewInterviewee;
import com.example.skaxis.interview.model.InterviewResult;
import com.example.skaxis.interview.repository.InterviewIntervieweeRepository;
import com.example.skaxis.interview.repository.InterviewRepository;
import com.example.skaxis.interview.repository.InterviewResultRepository;
import com.example.skaxis.question.dto.QuestionDto;
import com.example.skaxis.question.dto.StartInterviewRequestDto;
import com.example.skaxis.question.dto.StartInterviewResponseDto;
import com.example.skaxis.question.model.Question;
import com.example.skaxis.question.service.InternalQuestionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.example.skaxis.interview.service.InterviewService;
import com.example.skaxis.interview.service.IntervieweeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/interviews")
public class InterviewController {
    private final InterviewService interviewService;
    private final IntervieweeService intervieweeService;
    private final InternalQuestionService internalQuestionService; // 추가
    private final InterviewIntervieweeRepository interviewIntervieweeRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewResultRepository interviewResultRepository;
    private final WebClient webClient;

    // 전체 면접 및 연관 데이터 삭제 (관리자 권한 필요)
    @DeleteMapping("")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAllInterviews(
            @RequestParam(name = "deleteFiles", defaultValue = "true") boolean deleteFiles) {
        try {
            interviewService.deleteAllInterviews(deleteFiles);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            log.error("전체 면접 삭제 중 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "면접 전체 삭제 중 서버 오류가 발생했습니다."));
        }
    }

    // 기존 면접 관련 메서드들
    @GetMapping("/all")
    public ResponseEntity<?> getAllInterviews() {
        try {
            GetInterviewsResponseDto interviewList = interviewService.getAllInterviews();
            if (interviewList.getInterviewSessions() == null || interviewList.getInterviewSessions().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No interviews found"));
            }
            return ResponseEntity.ok(interviewList);
        } catch (Exception e) {
            log.error("Error fetching interviews: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal Server Error"));
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createInterview(@RequestBody CreateInterviewRequestDto createInterviewRequestDto) {
        try {
            if (createInterviewRequestDto == null || createInterviewRequestDto.getRoomNo() == null ||
                    createInterviewRequestDto.getRound() <= 0 || createInterviewRequestDto.getScheduledAt() == null ||
                    createInterviewRequestDto.getOrderNo() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid request data"));
            }
            interviewService.createInterview(createInterviewRequestDto);
            return ResponseEntity.ok().body(Map.of("message", "Interview created successfully"));
        } catch (Exception e) {
            log.error("Error creating interview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal Server Error"));
        }
    }

    @DeleteMapping("/{interviewId}")
    public ResponseEntity<?> deleteInterview(@PathVariable("interviewId") Long interviewId) {
        try {
            if (interviewId == null || interviewId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid interview ID"));
            }
            interviewService.deleteInterview(interviewId);
            return ResponseEntity.ok().body(Map.of("message", "Interview deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting interview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal Server Error"));
        }
    }

    @PutMapping("/{interviewId}")
    public ResponseEntity<?> updateInterview(@RequestBody UpdateInterviewRequestDto updateInterviewRequestDto,
            @PathVariable("interviewId") Long interviewId) {
        try {
            if (interviewId == null || interviewId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid interview ID"));
            }
            if (updateInterviewRequestDto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid request data"));
            }
            interviewService.updateInterview(updateInterviewRequestDto, interviewId);
            return ResponseEntity.ok().body(Map.of("message", "Interview updated successfully"));
        } catch (Exception e) {
            log.error("Error updating interview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal Server Error"));
        }
    }

    @GetMapping("/{interviewId}")
    public ResponseEntity<?> getInterviewById(@PathVariable("interviewId") Long interviewId) {
        try {
            if (interviewId == null || interviewId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid interview ID"));
            }
            GetInterviewByIdResponseDto interview = interviewService.getInterviewById(interviewId);
            if (interview == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Interview not found"));
            }
            return ResponseEntity.ok(interview);
        } catch (Exception e) {
            log.error("Error fetching interview by ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal Server Error"));
        }
    }

    // IntervieweeController에서 통합된 메서드들
    @GetMapping("/simple")
    public ResponseEntity<?> getInterviewees(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String position) {
        try {
            IntervieweeListResponseDto interviewees = intervieweeService.getInterviewees(date, status, position);
            if (interviewees == null || interviewees.getData().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No interviewees found");
            }
            return ResponseEntity.ok(interviewees);
        } catch (Exception e) {
            log.error("Error fetching interviewees: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/schedule")
    public ResponseEntity<?> getInterviewSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            SimpleInterviewScheduleResponseDto schedule = intervieweeService.getInterviewSchedule(date);
            if (schedule == null || schedule.getSchedules().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No schedule found for the given date");
            }
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            log.error("Error fetching interview schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("schedule/all")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllInterviewSchedules(
            @RequestParam(required = false) String status) {
        try {
            SimpleInterviewScheduleResponseDto schedules = intervieweeService.getAllInterviewSchedules(status);
            if (schedules == null || schedules.getSchedules().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No schedules found");
            }
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            log.error("Error fetching all interview schedules: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/schedule/detailed")
    public ResponseEntity<?> getDetailedInterviewSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            InterviewScheduleResponseDto detailedSchedule = intervieweeService.getDetailedInterviewSchedule(date);
            if (detailedSchedule == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No detailed schedule found for the given date");
            }
            return ResponseEntity.ok(detailedSchedule);
        } catch (Exception e) {
            log.error("Error fetching detailed interview schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    // @PutMapping("/{interviewId}/interviewees/{intervieweeId}")
    // public ResponseEntity<?> updateIntervieweeSchedule(
    // @PathVariable("interviewId") Long interviewId,
    // @PathVariable("intervieweeId") Long intervieweeId,
    // @RequestBody UpdateIntervieweeScheduleRequestDto requestDto) {
    // try {
    // if (interviewId == null || interviewId <= 0) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    // .body(Map.of("message", "Invalid interview ID"));
    // }
    // if (intervieweeId == null || intervieweeId <= 0) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    // .body(Map.of("message", "Invalid interviewee ID"));
    // }
    // if (requestDto == null || requestDto.getStartAt() == null ||
    // requestDto.getEndAt() == null) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    // .body(Map.of("message", "Invalid request data"));
    // }

    // interviewService.updateIntervieweeSchedule(interviewId, intervieweeId,
    // requestDto);
    // return ResponseEntity.ok().body(Map.of("message", "Interviewee schedule
    // updated successfully"));
    // } catch (Exception e) {
    // log.error("Error updating interviewee schedule: {}", e.getMessage());
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body(Map.of("message", "Internal Server Error"));
    // }
    // }

    @Transactional
    @PostMapping("/start")
    @Operation(summary = "면접 시작", description = "각 지원자의 질문 목록을 로드해 반환하고, 면접 상태를 초기화합니다.")
    public ResponseEntity<?> startInterview(@RequestBody StartInterviewRequestDto request) {
        try {
            log.info("면접 시작 요청: {}", request);

            if (request.getIntervieweeIds() == null || request.getIntervieweeIds().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "지원자 ID 목록이 필요합니다."));
            }

            List<Long> intervieweeIds = request.getIntervieweeIds();

            // 면접 상태를 COMPLETED로 변경
            for (Long intervieweeId : intervieweeIds) {
                List<InterviewInterviewee> interviewInterviewees = interviewIntervieweeRepository
                        .findByIntervieweeId(intervieweeId);

                for (InterviewInterviewee ii : interviewInterviewees) {
                    Interview interview = ii.getInterview();
                    if (interview != null) {
                        interview.setStatus(Interview.InterviewStatus.COMPLETED);
                        interviewRepository.save(interview);
                    }
                }
            }

            // InternalQuestionService를 사용하여 질문 조회
            Map<String, List<Question>> questionsPerInterviewee = internalQuestionService
                    .getQuestionsForMultipleInterviewees(intervieweeIds);

            // Question을 QuestionDto로 변환
            Map<String, List<QuestionDto>> questionDtosPerInterviewee = new HashMap<>();
            for (Map.Entry<String, List<Question>> entry : questionsPerInterviewee.entrySet()) {
                List<QuestionDto> questionDtos = entry.getValue().stream()
                        .map(question -> new QuestionDto(
                                question.getQuestionId().intValue(),
                                mapTypeToFrontend(question.getType()),
                                question.getContent()))
                        .toList();
                questionDtosPerInterviewee.put(entry.getKey(), questionDtos);
            }

            // ✅ FastAPI 서버로 면접 상태 초기화 요청 (수정된 구조)
            try {
                // Convert Long to Integer for FastAPI compatibility
                List<Integer> fastApiIntervieweeIds = intervieweeIds.stream()
                        .map(Long::intValue)
                        .toList();

                Map<String, Object> fastApiRequest = Map.of(
                        "interviewee_ids", fastApiIntervieweeIds);

                webClient.post()
                        .uri("http://fastapi:8000/api/v1/interview/start")
                        .bodyValue(fastApiRequest)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(10))
                        .block();

                log.info("FastAPI 면접 상태 초기화 완료: {}", intervieweeIds);
            } catch (Exception fastApiError) {
                log.error("FastAPI 호출 실패: {}", fastApiError.getMessage());
                // FastAPI 호출 실패해도 Spring Boot 응답은 정상 반환
            }

            StartInterviewResponseDto response = new StartInterviewResponseDto(
                    questionDtosPerInterviewee,
                    "success");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("면접 시작 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("면접 시작 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "면접 시작 중 오류가 발생했습니다."));
        }
    }

    /**
     * 데이터베이스의 타입을 프론트엔드가 기대하는 형태로 변환
     */
    private String mapTypeToFrontend(String dbType) {
        if ("공통질문".equals(dbType)) {
            return "공통질문";
        } else if ("개별질문".equals(dbType)) {
            return "개별질문";
        }
        return dbType; // 기본값
    }

    @DeleteMapping("/{interviewId}/interviewees/{intervieweeId}")
    public ResponseEntity<?> deleteInterviewInterviewee(
            @PathVariable("interviewId") Long interviewId,
            @PathVariable("intervieweeId") Long intervieweeId) {
        try {
            if (interviewId == null || interviewId <= 0 || intervieweeId == null || intervieweeId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid interview ID or interviewee ID"));
            }

            interviewService.deleteInterviewInterviewee(interviewId, intervieweeId);
            return ResponseEntity.ok().body(Map.of("message", "Interview-Interviewee mapping deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting interview-interviewee mapping: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal Server Error"));
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeInterview(@RequestBody CompleteInterviewRequestDto request) {
        try {
            log.info("면접 완료 처리 시작: {}", request);

            if (request.getIntervieweeIds() == null || request.getIntervieweeIds().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "지원자 ID 목록이 필요합니다."));
            }

            List<Long> intervieweeIds = request.getIntervieweeIds();
            List<String> successResults = new ArrayList<>();
            List<String> failedResults = new ArrayList<>();

            for (Long intervieweeId : intervieweeIds) {
                try {
                    // FastAPI에서 결과 조회
                    String fastApiUrl = "http://fastapi:8000/api/v1/results/?interviewee_ids=" + intervieweeId;
                    log.info("FastAPI 결과 조회 시작: intervieweeId={}, url={}", intervieweeId, fastApiUrl);

                    String resultJson = webClient.get()
                            .uri(fastApiUrl)
                            .retrieve()
                            .bodyToMono(String.class)
                            .timeout(Duration.ofSeconds(30))
                            .block();

                    if (resultJson == null || resultJson.trim().isEmpty()) {
                        log.warn("FastAPI에서 빈 응답 받음: intervieweeId={}", intervieweeId);
                        failedResults.add("지원자 " + intervieweeId + ": 빈 응답");
                        continue;
                    }

                    log.info("FastAPI 응답 받음: intervieweeId={}, responseLength={}", intervieweeId, resultJson.length());

                    // JSON 파싱 후 InterviewResult 테이블에 저장
                    boolean saved = saveInterviewResult(intervieweeId, resultJson);

                    if (saved) {
                        successResults.add("지원자 " + intervieweeId + ": 저장 완료");
                        log.info("면접 결과 저장 완료: intervieweeId={}", intervieweeId);
                    } else {
                        failedResults.add("지원자 " + intervieweeId + ": 저장 실패");
                    }

                } catch (Exception e) {
                    log.error("FastAPI 결과 조회/저장 실패: intervieweeId={}, error={}", intervieweeId, e.getMessage(), e);
                    failedResults.add("지원자 " + intervieweeId + ": " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "면접 완료 처리됨");
            response.put("total", intervieweeIds.size());
            response.put("success", successResults.size());
            response.put("failed", failedResults.size());

            if (!successResults.isEmpty()) {
                response.put("successDetails", successResults);
            }
            if (!failedResults.isEmpty()) {
                response.put("failedDetails", failedResults);
            }

            log.info("면접 완료 처리 결과: 성공={}, 실패={}", successResults.size(), failedResults.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("면접 완료 처리 중 전체 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "면접 완료 처리 중 오류 발생: " + e.getMessage()));
        }
    }

    private boolean saveInterviewResult(Long intervieweeId, String resultJson) {
        try {
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(resultJson);

            // FastAPI 응답 구조: {"results": [{"interviewee_id": 24, "score": 85, ...}]}
            JsonNode resultsArray = rootNode.get("results");
            if (resultsArray == null || !resultsArray.isArray() || resultsArray.size() == 0) {
                log.warn("FastAPI 응답에 results 배열이 없음: intervieweeId={}", intervieweeId);
                return false;
            }

            JsonNode resultNode = resultsArray.get(0); // 첫 번째 결과 사용

            // 필수 필드 추출
            int score = resultNode.path("score").asInt(0);
            String pdfPath = resultNode.path("pdf_path").asText("");
            String excelPath = resultNode.path("excel_path").asText("");
            String sttPath = resultNode.path("stt_path").asText("");

            // 언어적/비언어적 평가 결과 추출
            JsonNode languageEval = resultNode.path("language_evaluation");
            JsonNode nonverbalEval = resultNode.path("nonverbal_evaluation");

            String comment = String.format(
                    "언어적 평가: %s (점수: %d)\n비언어적 평가: %s (점수: %d)",
                    languageEval.path("reason").asText("평가 없음"),
                    languageEval.path("score").asInt(0),
                    nonverbalEval.path("reason").asText("평가 없음"),
                    nonverbalEval.path("score").asInt(0));

            // 면접 정보 조회 (intervieweeId로 interview 찾기)
            List<InterviewInterviewee> interviewInterviewees = interviewIntervieweeRepository
                    .findByIntervieweeId(intervieweeId);

            if (interviewInterviewees.isEmpty()) {
                log.warn("해당 지원자의 면접 정보를 찾을 수 없음: intervieweeId={}", intervieweeId);
                return false;
            }

            InterviewInterviewee interviewInterviewee = interviewInterviewees.get(0);
            Long interviewId = interviewInterviewee.getInterview().getInterviewId();

            // 기존 InterviewResult 조회 또는 새로 생성
            Optional<InterviewResult> existingResult = interviewResultRepository
                    .findByInterviewIdAndIntervieweeId(interviewId, intervieweeId);

            InterviewResult interviewResult;
            if (existingResult.isPresent()) {
                interviewResult = existingResult.get();
                log.info("기존 InterviewResult 업데이트: interviewId={}, intervieweeId={}", interviewId, intervieweeId);
            } else {
                // 새로운 InterviewResult 생성
                InterviewResult newResult = new InterviewResult();
                newResult.setInterviewee(interviewInterviewee.getInterviewee());
                newResult.setInterview(interviewInterviewee.getInterview());
                // 🔥 ID 필드 명시적 설정
                newResult.setInterviewId(interviewId);
                newResult.setIntervieweeId(intervieweeId);
                
                interviewResult = newResult;
                log.info("새 InterviewResult 생성: interviewId={}, intervieweeId={}", interviewId, intervieweeId);
            }

            // 결과 데이터 설정
            interviewResult.setScore(score);
            interviewResult.setComment(comment);
            interviewResult.setPdfPath(pdfPath.isEmpty() ? null : pdfPath);
            interviewResult.setExcelPath(excelPath.isEmpty() ? null : excelPath);
            interviewResult.setSttPath(sttPath.isEmpty() ? null : sttPath);

            // 데이터베이스에 저장
            interviewResultRepository.save(interviewResult);

            log.info("InterviewResult 저장 완료: resultId={}, score={}",
                    interviewResult.getResultId(), score);

            return true;

        } catch (Exception e) {
            log.error("InterviewResult 저장 중 오류: intervieweeId={}, error={}", intervieweeId, e.getMessage(), e);
            return false;
        }
    }
}
