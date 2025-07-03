package com.example.skaxis.interview.dto.interviewee;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateIntervieweeRequestDto {
    private String name;
    private Integer score;
    private Long interviewId;  // 면접 ID 추가
    private LocalDateTime startAt;  // 면접 시작 시간 추가
    private LocalDateTime endAt;    // 면접 종료 시간 추가
}