package com.example.skaxis.interview.dto.interview;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UpdateIntervieweeScheduleRequestDto {
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}