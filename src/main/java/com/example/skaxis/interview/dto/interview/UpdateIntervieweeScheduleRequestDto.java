package com.example.skaxis.interview.dto.interview;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateIntervieweeScheduleRequestDto {
    private LocalDateTime scheduledAt;
}