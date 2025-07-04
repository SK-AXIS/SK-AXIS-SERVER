package com.example.skaxis.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateIntervieweeRequestDto {
    private String name;
    private String job;
    private String roomId;
    private String interviewers;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startAt;  // 면접 시작 시간 추가
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endAt;    // 면접 종료 시간 추가
    private Integer score;
    private String status;  // 면접 상태 추가 (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, UNDECIDED)
}
