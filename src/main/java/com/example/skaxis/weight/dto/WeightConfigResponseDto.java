package com.example.skaxis.weight.dto;

import com.example.skaxis.weight.model.WeightConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightConfigResponseDto {
    
    private Long weightConfigId;
    private Integer verbalWeight;    // 언어적 요소 (%)
    private Integer domainWeight;    // 직무·도메인 (%)
    private Integer nonverbalWeight; // 비언어적 요소 (%)
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    // Entity -> DTO 변환 메서드
    public static WeightConfigResponseDto from(WeightConfig weightConfig) {
        return WeightConfigResponseDto.builder()
                .weightConfigId(weightConfig.getWeightConfigId())
                .verbalWeight(weightConfig.getVerbalWeight())
                .domainWeight(weightConfig.getDomainWeight())
                .nonverbalWeight(weightConfig.getNonverbalWeight())
                .isActive(weightConfig.getIsActive())
                .createdAt(weightConfig.getCreatedAt())
                .build();
    }
}