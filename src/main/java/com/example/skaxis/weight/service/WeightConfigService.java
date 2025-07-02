package com.example.skaxis.weight.service;

import com.example.skaxis.weight.dto.WeightConfigCreateRequestDto;
import com.example.skaxis.weight.dto.WeightConfigResponseDto;
import com.example.skaxis.weight.model.WeightConfig;
import com.example.skaxis.weight.repository.WeightConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeightConfigService {
    
    private final WeightConfigRepository weightConfigRepository;
    
    /**
     * 현재 활성화된 가중치 설정 조회
     */
    @Transactional(readOnly = true)
    public WeightConfigResponseDto getActiveWeightConfig() {
        WeightConfig activeConfig = weightConfigRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("활성화된 가중치 설정이 없습니다"));
        
        return WeightConfigResponseDto.from(activeConfig);
    }
    
    /**
     * 모든 가중치 설정 조회 (최신순)
     */
    @Transactional(readOnly = true)
    public List<WeightConfigResponseDto> getAllWeightConfigs() {
        List<WeightConfig> configs = weightConfigRepository.findAll();
        return configs.stream()
                .map(WeightConfigResponseDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 새로운 가중치 설정 생성
     */
    @Transactional
    public WeightConfigResponseDto createWeightConfig(WeightConfigCreateRequestDto requestDto) {
        // 가중치 합계 검증
        if (!requestDto.isValidWeightSum()) {
            throw new IllegalArgumentException("가중치의 합은 100이어야 합니다");
        }
        
        // 기존 활성화된 설정 비활성화
        weightConfigRepository.findByIsActiveTrue()
                .ifPresent(existingConfig -> {
                    existingConfig.setIsActive(false);
                    weightConfigRepository.save(existingConfig);
                });
        
        // 새로운 설정 생성 및 활성화
        WeightConfig newConfig = WeightConfig.builder()
                .verbalWeight(requestDto.getVerbalWeight())
                .domainWeight(requestDto.getDomainWeight())
                .nonverbalWeight(requestDto.getNonverbalWeight())
                .isActive(true)
                .build();
        
        WeightConfig savedConfig = weightConfigRepository.save(newConfig);
        log.info("새로운 가중치 설정이 생성되었습니다. ID: {}", savedConfig.getWeightConfigId());
        
        return WeightConfigResponseDto.from(savedConfig);
    }
    
    /**
     * 기본 가중치 설정 초기화 (시스템 초기 설정용)
     */
    @Transactional
    public WeightConfigResponseDto initializeDefaultWeightConfig() {
        // 이미 설정이 있는지 확인
        if (weightConfigRepository.existsByIsActiveTrue()) {
            return getActiveWeightConfig();
        }
        
        // 기본 가중치 설정 (예: 언어적 40%, 직무·도메인 40%, 비언어적 20%)
        WeightConfigCreateRequestDto defaultRequest = new WeightConfigCreateRequestDto(40, 40, 20);
        return createWeightConfig(defaultRequest);
    }
}