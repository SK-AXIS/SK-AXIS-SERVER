package com.example.skaxis.interview.service;

import com.example.skaxis.interview.dto.CreateInterviewRequestDto;
import com.example.skaxis.interview.dto.GetInterviewByIdResponseDto;
import com.example.skaxis.interview.dto.GetInterviewsResponseDto;
import com.example.skaxis.interview.dto.UpdateInterviewRequestDto;
import com.example.skaxis.interview.dto.interview.UpdateIntervieweeScheduleRequestDto;
import com.example.skaxis.interview.model.Interview;
import com.example.skaxis.interview.model.InterviewInterviewee;
import com.example.skaxis.interview.model.Interviewee;
import com.example.skaxis.interview.repository.InterviewRepository;
import com.example.skaxis.interview.repository.IntervieweeRepository;
import com.example.skaxis.question.model.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

import com.example.skaxis.user.model.User;
import com.example.skaxis.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final com.example.skaxis.interview.repository.InterviewIntervieweeRepository interviewIntervieweeRepository;
    private final com.example.skaxis.interview.repository.InterviewResultRepository interviewResultRepository;
    private final com.example.skaxis.question.repository.QuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;
    private final IntervieweeRepository intervieweeRepository;
    private final UserRepository userRepository;
    @Transactional
    public void updateIntervieweeSchedule(Long interviewId, Long intervieweeId, UpdateIntervieweeScheduleRequestDto requestDto) {
        InterviewInterviewee interviewInterviewee = interviewIntervieweeRepository.findByInterviewIdAndIntervieweeId(interviewId, intervieweeId)
                .orElseThrow(() -> new RuntimeException("Interview-Interviewee mapping not found"));
    
        Interview originalInterview = interviewInterviewee.getInterview();
    
        // 새로운 Interview 객체 생성
        Interview newInterview = new Interview();
        newInterview.setRoomNo(originalInterview.getRoomNo());
        newInterview.setRound(originalInterview.getRound());
        newInterview.setOrderNo(originalInterview.getOrderNo());
        newInterview.setStatus(originalInterview.getStatus());
        newInterview.setInterviewers(originalInterview.getInterviewers());
        newInterview.setScheduledAt(requestDto.getStartAt()); // startAt을 면접 시작 시간으로 설정
        newInterview.setScheduledEndAt(requestDto.getEndAt()); // endAt을 면접 종료 시간으로 설정
    
        Interview savedNewInterview = interviewRepository.save(newInterview);
    
        // 기존 연결 정보 업데이트
        interviewInterviewee.setInterview(savedNewInterview);
        interviewIntervieweeRepository.save(interviewInterviewee);
    
        // 기존 면접에 더 이상 면접자가 없는 경우 삭제
        long remainingInterviewees = interviewIntervieweeRepository.countByInterviewId(originalInterview.getInterviewId());
        if (remainingInterviewees == 0) {
            interviewRepository.delete(originalInterview);
        }
    }

    @Transactional(readOnly = true)
    public GetInterviewsResponseDto getAllInterviews() {
        List<Interview> interviewList = interviewRepository.findAll();

        GetInterviewsResponseDto getInterviewsResponseDto = new GetInterviewsResponseDto();
        for (Interview interview : interviewList) {
            GetInterviewsResponseDto.InterviewSession interviewSession = new GetInterviewsResponseDto.InterviewSession();
            interviewSession.setInterviewId(interview.getInterviewId());
            interviewSession.setRoomNo(interview.getRoomNo());
            interviewSession.setRound(interview.getRound());
            interviewSession.setScheduledAt(interview.getScheduledAt().toString());
            interviewSession.setOrderNo(interview.getOrderNo());
            interviewSession.setStatus(interview.getStatus().name());
            interviewSession.setCreatedAt(interview.getCreatedAt().toString());

            List<Interviewee> intervieweeList = interview.getInterviewInterviewees()
                .stream()
                .map(i -> i.getInterviewee())
                .toList();
            interviewSession.setInterviewees(intervieweeList.toArray(new Interviewee[0]));

            // 면접관 정보를 문자열로만 처리 (User 엔티티 사용하지 않음)
            String interviewersStr = interview.getInterviewers();
            if (interviewersStr != null && !interviewersStr.isEmpty()) {
                interviewersStr.split(",");
                // User 배열 대신 문자열 배열로 처리하거나, 더미 User 객체 생성
                // GetInterviewsResponseDto.InterviewSession의 setInterviewers가 User[] 타입을 받는다면
                // 더미 User 객체를 생성해야 합니다
                interviewSession.setInterviewers(new User[0]); // 임시로 빈 배열 설정
            } else {
                interviewSession.setInterviewers(new User[0]);
            }

            getInterviewsResponseDto.getInterviewSessions().add(interviewSession);
        }
        return getInterviewsResponseDto;
    }

    public void createInterview(CreateInterviewRequestDto createInterviewRequestDto) {
        Interview interview = new Interview();
        interview.setRoomNo(createInterviewRequestDto.getRoomNo());
        interview.setRound(createInterviewRequestDto.getRound());
        interview.setScheduledAt(java.time.LocalDateTime.parse(createInterviewRequestDto.getScheduledAt()));
        interview.setOrderNo(createInterviewRequestDto.getOrderNo());
        interview.setStatus(Interview.InterviewStatus.SCHEDULED);

        interviewRepository.save(interview);
    }

    @Transactional
    public void deleteInterviewInterviewee(Long interviewId, Long intervieweeId) {
        // 1. InterviewInterviewee 찾기
        InterviewInterviewee interviewInterviewee = interviewIntervieweeRepository
            .findByInterviewIdAndIntervieweeId(interviewId, intervieweeId)
            .orElseThrow(() -> new RuntimeException("Interview-Interviewee mapping not found"));
        
        // 2. 관련된 Question 삭제
        List<Question> questions = questionRepository.findByInterviewId(interviewId);
        questionRepository.deleteAll(questions);
        
        // 3. InterviewInterviewee 삭제
        interviewIntervieweeRepository.delete(interviewInterviewee);
        
        // 4. 해당 Interview에 더 이상 InterviewInterviewee가 없으면 Interview도 삭제
        long remainingCount = interviewIntervieweeRepository.countByInterviewId(interviewId);
        if (remainingCount == 0) {
            interviewRepository.deleteById(interviewId);
        }
    }

    // 기존 deleteInterview 메소드 개선
    @Transactional
    public void deleteInterview(Long interviewId) {
        // 1. 관련된 Question 삭제
        List<Question> questions = questionRepository.findByInterviewId(interviewId);
        questionRepository.deleteAll(questions);
        
        // 2. 관련된 InterviewInterviewee 삭제
        List<InterviewInterviewee> interviewInterviewees = interviewIntervieweeRepository.findByInterviewId(interviewId);
        interviewIntervieweeRepository.deleteAll(interviewInterviewees);
        
        // 3. Interview 삭제
        interviewRepository.deleteById(interviewId);
    }

    public void updateInterview(UpdateInterviewRequestDto updateInterviewRequestDto, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found"));
    
        if (updateInterviewRequestDto.getRoomNo() != null) {
            interview.setRoomNo(updateInterviewRequestDto.getRoomNo());
        }
        if (updateInterviewRequestDto.getRound() != null) {
            interview.setRound(updateInterviewRequestDto.getRound());
        }
        if (updateInterviewRequestDto.getScheduledAt() != null) {
            interview.setScheduledAt(java.time.LocalDateTime.parse(updateInterviewRequestDto.getScheduledAt()));
        }
        if (updateInterviewRequestDto.getOrderNo() != null) {
            interview.setOrderNo(updateInterviewRequestDto.getOrderNo());
        }
        if (updateInterviewRequestDto.getStatus() != null) {
            interview.setStatus(Interview.InterviewStatus.valueOf(updateInterviewRequestDto.getStatus()));
        }
        if (updateInterviewRequestDto.getIntervieweeIds() != null) {
            for (Long intervieweeId : updateInterviewRequestDto.getIntervieweeIds()) {
                Interviewee interviewee = intervieweeRepository.findById(intervieweeId)
                    .orElseThrow(() -> new RuntimeException("Interviewee not found with ID: " + intervieweeId));
                InterviewInterviewee interviewInterviewee = new InterviewInterviewee();
                interviewInterviewee.setInterview(interview);
                interviewInterviewee.setInterviewee(interviewee);
                interview.getInterviewInterviewees().add(interviewInterviewee);
                //TODO: Handle score, comment, pdfPath, excelPath, sttPath if needed
            }
        }
    
        // interviewerIds 관련 코드 제거됨
        
        interviewRepository.save(interview);
    }

    public GetInterviewByIdResponseDto getInterviewById(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found with ID: " + interviewId));
        GetInterviewByIdResponseDto getInterviewByIdResponseDto = new GetInterviewByIdResponseDto();
        getInterviewByIdResponseDto.setInterviewId(interview.getInterviewId());
        getInterviewByIdResponseDto.setRoomNo(interview.getRoomNo());
        getInterviewByIdResponseDto.setRound(interview.getRound());
        getInterviewByIdResponseDto.setScheduledAt(interview.getScheduledAt().toString());
        getInterviewByIdResponseDto.setOrderNo(interview.getOrderNo());
        getInterviewByIdResponseDto.setStatus(interview.getStatus().name());
        getInterviewByIdResponseDto.setCreatedAt(interview.getCreatedAt().toString());
        getInterviewByIdResponseDto.setInterviewees(interview.getInterviewInterviewees()
            .stream()
            .map(i -> new GetInterviewByIdResponseDto.IntervieweeDto(
                i.getInterviewee().getIntervieweeId(),
                i.getInterviewee().getName(),
//                i.getInterviewee().getApplicantCode(),
                i.getCreatedAt().toString()))
            .toArray(GetInterviewByIdResponseDto.IntervieweeDto[]::new));

        // 면접관 정보를 문자열로만 처리 (User 엔티티 사용하지 않음)
        String interviewersStr = interview.getInterviewers();
        if (interviewersStr != null && !interviewersStr.isEmpty()) {
            String[] interviewerNames = interviewersStr.split(",");
            List<GetInterviewByIdResponseDto.InterviewerDto> interviewerDtos = new ArrayList<>();
            for (int i = 0; i < interviewerNames.length; i++) {
                String name = interviewerNames[i].trim();
                // User 엔티티를 사용하지 않으므로 더미 데이터로 설정
                interviewerDtos.add(new GetInterviewByIdResponseDto.InterviewerDto(
                    (long) (i + 1), // 더미 userId
                    name, // userName으로 name 사용
                    name, // name
                    "INTERVIEWER", // 기본 userType
                    LocalDateTime.now().toString() // 더미 createdAt
                ));
            }
            getInterviewByIdResponseDto.setInterviewers(interviewerDtos.toArray(new GetInterviewByIdResponseDto.InterviewerDto[0]));
        } else {
            getInterviewByIdResponseDto.setInterviewers(new GetInterviewByIdResponseDto.InterviewerDto[0]);
        }

        return getInterviewByIdResponseDto;
    }

    // 공통 유틸리티 메서드들
    public Interview findInterviewById(Long interviewId) {
        return interviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found with ID: " + interviewId));
    }

    public boolean existsById(Long interviewId) {
        return interviewRepository.existsById(interviewId);
    }

    public List<Interview> findInterviewsByDate(LocalDate date) {
        return interviewRepository.findByScheduledDate(date);
    }

    public List<String> findDistinctRoomIdsByDate(LocalDate date) {
        return interviewRepository.findDistinctRoomIdsByDate(date);
    }

    public List<String> findDistinctRoomIds() {
        return interviewRepository.findDistinctRoomIds();
    }

    public List<Interview> findByStatusOrderByScheduledAt(Interview.InterviewStatus status) {
        return interviewRepository.findByStatusOrderByScheduledAt(status);
    }

    public List<Interview> findAllOrderByScheduledAt() {
        return interviewRepository.findAllOrderByScheduledAt();
    }

    public Interview findById(Long recentInterviewId) {
        return interviewRepository.findById(recentInterviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found with ID: " + recentInterviewId));
    }

    // === 전체 면접 및 연관 데이터 삭제 ===
    @Transactional
    public void deleteAllInterviews(boolean deleteFiles) {
        // InterviewResult 파일 및 데이터 삭제
        if (deleteFiles) {
            interviewResultRepository.findAll().forEach(result -> {
                deleteFileIfExists(result.getPdfPath());
                deleteFileIfExists(result.getExcelPath());
                deleteFileIfExists(result.getSttPath());
            });
        }
        interviewResultRepository.deleteAll();

        // InterviewInterviewee 파일 및 데이터 삭제
        if (deleteFiles) {
            interviewIntervieweeRepository.findAll().forEach(ii -> {
                deleteFileIfExists(ii.getPdfPath());
                deleteFileIfExists(ii.getExcelPath());
                deleteFileIfExists(ii.getSttPath());
            });
        }
        interviewIntervieweeRepository.deleteAll();

        // Question 전체 삭제
        questionRepository.deleteAll();

        // Interview 전체 삭제
        interviewRepository.deleteAll();
    }

    private void deleteFileIfExists(String path) {
        if (path != null && !path.isBlank()) {
            try {
                java.io.File file = new java.io.File(path);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                log.warn("파일 삭제 실패: {}", path);
            }
        }
    }

    public Interview saveInterview(Interview interview) {
        if (interview.getCreatedAt() == null) {
            interview.setCreatedAt(LocalDateTime.now());
        }
        return interviewRepository.save(interview);
    }
}