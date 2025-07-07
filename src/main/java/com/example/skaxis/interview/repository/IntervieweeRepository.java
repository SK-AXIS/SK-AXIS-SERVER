package com.example.skaxis.interview.repository;

import com.example.skaxis.interview.model.InterviewInterviewee;
import com.example.skaxis.interview.model.Interviewee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntervieweeRepository extends JpaRepository<Interviewee, Long> {
    // 이 메서드는 IntervieweeRepository에 있으면 안 됩니다. 제거하거나 올바른 반환 타입으로 수정
    // List<InterviewInterviewee> findByIntervieweeId(Long intervieweeId); // 제거
    
    Optional<Interviewee> findByName(String name);
    boolean existsByName(String name);
}