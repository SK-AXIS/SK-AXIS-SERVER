package com.example.skaxis.weight.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "weight_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeightConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weight_config_id")
    private Long weightConfigId;
    
    @Column(name = "verbal_weight", nullable = false)
    private Integer verbalWeight; // 언어적 요소 (%)
    
    @Column(name = "domain_weight", nullable = false)
    private Integer domainWeight; // 직무·도메인 (%)
    
    @Column(name = "nonverbal_weight", nullable = false)
    private Integer nonverbalWeight; // 비언어적 요소 (%)
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true; // 현재 사용 중 여부
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // 가중치 합계 검증 메서드
    public boolean isValidWeightSum() {
        return (verbalWeight + domainWeight + nonverbalWeight) == 100;
    }
}