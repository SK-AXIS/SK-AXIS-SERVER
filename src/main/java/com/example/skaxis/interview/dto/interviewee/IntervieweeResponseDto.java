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
    private LocalDateTime scheduledStartAt;
    private LocalDateTime scheduledEndAt;
    private Interview.InterviewStatus status;
    private Integer score;
    private String interviewers;
    private String roomNo;
    private String comment;
    private LocalDateTime createdAt;

    public static IntervieweeResponseDto from(InterviewInterviewee interviewInterviewee) {
        Interview interview = interviewInterviewee.getInterview();
        Interviewee interviewee = interviewInterviewee.getInterviewee();
    
        return IntervieweeResponseDto.builder()
                .interviewId(interview.getInterviewId())
                .intervieweeId(interviewee.getIntervieweeId())
                .name(interviewee.getName())  // ✅ Interviewee에서 가져오기
                .scheduledStartAt(interview.getScheduledAt())  // ✅ Interview에서 가져오기
                .scheduledEndAt(interview.getScheduledEndAt())  // ✅ Interview에서 가져오기
                .status(interview.getStatus())  // ✅ Interview에서 가져오기
                .score(interviewee.getScore())  // ✅ 수정: Interviewee에서 가져오기
                .interviewers(interview.getInterviewers())  // ✅ Interview에서 가져오기
                .roomNo(interview.getRoomNo())  // ✅ Interview에서 가져오기
                .comment(interviewInterviewee.getComment())  // ✅ InterviewInterviewee에서 가져오기
                .createdAt(interviewInterviewee.getCreatedAt())  // ✅ InterviewInterviewee에서 가져오기
                .build();
    }

    public static List<IntervieweeResponseDto> fromList(List<InterviewInterviewee> interviewInterviewees) {
        return interviewInterviewees.stream()
                .map(IntervieweeResponseDto::from)
                .collect(Collectors.toList());
    }
}