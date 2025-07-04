package com.example.skaxis.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInterviewRequestDto {
    private String roomNo;
    private Integer round;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer orderNo;
    private String status;
    private Long[] intervieweeIds;
}
