package com.example.skaxis.interview.dto.interviewee;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
public class UpdateIntervieweeRequestDto {
    private String name;
    private String job;
    private String status;
    private LocalDateTime startAt;  // 면접 시작 시간 추가
    private LocalDateTime endAt;    // 면접 종료 시간 추가
    private String interviewers;
    private String roomName;
    private Integer score;
}