package com.example.skaxis.interview.dto.interviewee;

import com.example.skaxis.interview.model.Interview;
import com.example.skaxis.interview.model.InterviewInterviewee;
import com.example.skaxis.interview.model.Interviewee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntervieweeResponseDto {
    private Long interviewId;
    private Long intervieweeId;
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Interview.InterviewStatus status;
    private Integer score;
    private String interviewers;
    private String roomNo;
    private String comment;
    private LocalDateTime createdAt;

    public static IntervieweeResponseDto from(InterviewInterviewee interviewInterviewee) {
        Interviewee interviewee = interviewInterviewee.getInterviewee();
        Interview interview = interviewInterviewee.getInterview();
    
        return IntervieweeResponseDto.builder()
            .interviewId(interview.getInterviewId())
            .intervieweeId(interviewee.getIntervieweeId())
            .name(interviewee.getName())
            .score(interviewee.getScore()) // 수정: interviewInterviewee.getScore() → interviewee.getScore()
            .roomNo(interview.getRoomNo())
            .startAt(interview.getScheduledAt())
            .endAt(interview.getScheduledEndAt())
            .status(interview.getStatus())
            .interviewers(interview.getInterviewers())
            .createdAt(interviewee.getCreatedAt())
            .build();
    }

    public static List<IntervieweeResponseDto> fromList(List<InterviewInterviewee> interviewInterviewees) {
        return interviewInterviewees.stream()
                .map(IntervieweeResponseDto::from)
                .collect(Collectors.toList());
    }
}