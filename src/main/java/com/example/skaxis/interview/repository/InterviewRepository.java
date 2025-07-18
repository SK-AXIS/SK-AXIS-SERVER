package com.example.skaxis.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.skaxis.interview.model.Interview;

import java.time.LocalDate;
import java.util.List;

import com.example.skaxis.interview.model.Interview.InterviewStatus;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    @Query("SELECT i FROM Interview i WHERE DATE(i.scheduledAt) = :date ORDER BY i.scheduledAt")
    List<Interview> findByScheduledDate(@Param("date") LocalDate date);

    // 모든 면접 일정을 시간순으로 조회
    @Query("SELECT i FROM Interview i ORDER BY i.scheduledAt")
    List<Interview> findAllOrderByScheduledAt();

    // 상태별 면접 일정 조회
    @Query("SELECT i FROM Interview i WHERE i.status = :status ORDER BY i.scheduledAt")
    List<Interview> findByStatusOrderByScheduledAt(@Param("status") InterviewStatus status);

    // 고유한 면접실 ID들을 조회하는 메서드 추가
    @Query("SELECT DISTINCT i.roomNo FROM Interview i WHERE i.roomNo IS NOT NULL ORDER BY i.roomNo")
    List<String> findDistinctRoomIds();

    // 특정 날짜의 고유한 면접실 ID들을 조회하는 메서드 추가
    @Query("SELECT DISTINCT i.roomNo FROM Interview i WHERE DATE(i.scheduledAt) = :date AND i.roomNo IS NOT NULL ORDER BY i.roomNo")
    List<String> findDistinctRoomIdsByDate(@Param("date") LocalDate date);
}