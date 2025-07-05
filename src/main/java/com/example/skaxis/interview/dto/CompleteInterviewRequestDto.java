package com.example.skaxis.interview.dto;

import lombok.Data;
import java.util.List;

@Data
public class CompleteInterviewRequestDto {
    private List<Long> intervieweeIds;
}
