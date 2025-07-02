package com.example.skaxis.weight.repository;

import com.example.skaxis.weight.model.WeightConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeightConfigRepository extends JpaRepository<WeightConfig, Long> {
    
    // 현재 활성화된 가중치 설정 조회
    Optional<WeightConfig> findByIsActiveTrue();
    
    // 최신 가중치 설정 조회 (생성일 기준)
    @Query("SELECT w FROM WeightConfig w ORDER BY w.createdAt DESC LIMIT 1")
    Optional<WeightConfig> findLatest();
    
    // 활성화된 설정이 있는지 확인
    boolean existsByIsActiveTrue();
}