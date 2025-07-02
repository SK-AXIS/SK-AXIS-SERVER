package com.example.skaxis.interview.dto.interviewee;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateIntervieweeRequestDto {
    private String name;
    private Integer score;
}