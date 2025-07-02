package com.example.skaxis.weight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightConfigCreateRequestDto {
    
    @NotNull(message = "언어적 요소 가중치는 필수입니다")
    @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
    @Max(value = 100, message = "가중치는 100 이하여야 합니다")
    private Integer verbalWeight;
    
    @NotNull(message = "직무·도메인 가중치는 필수입니다")
    @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
    @Max(value = 100, message = "가중치는 100 이하여야 합니다")
    private Integer domainWeight;
    
    @NotNull(message = "비언어적 요소 가중치는 필수입니다")
    @Min(value = 0, message = "가중치는 0 이상이어야 합니다")
    @Max(value = 100, message = "가중치는 100 이하여야 합니다")
    private Integer nonverbalWeight;
    
    // 가중치 합계 검증
    public boolean isValidWeightSum() {
        return (verbalWeight + domainWeight + nonverbalWeight) == 100;
    }
}