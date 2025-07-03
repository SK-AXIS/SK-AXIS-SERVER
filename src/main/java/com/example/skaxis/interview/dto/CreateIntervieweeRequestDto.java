package com.example.skaxis.interview.dto;

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
    private LocalDateTime startAt;  // 면접 시작 시간 추가
    private LocalDateTime endAt;    // 면접 종료 시간 추가
    private Integer score;

}
