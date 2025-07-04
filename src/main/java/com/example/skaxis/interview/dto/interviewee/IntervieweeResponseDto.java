package com.example.skaxis.interview.dto.interviewee;

import com.example.skaxis.interview.model.Interview;
import com.example.skaxis.interview.model.InterviewInterviewee;
import com.example.skaxis.interview.model.Interviewee;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endAt;
    private Interview.InterviewStatus status;
    private Integer score;
    private String interviewers;
    private String roomNo;
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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