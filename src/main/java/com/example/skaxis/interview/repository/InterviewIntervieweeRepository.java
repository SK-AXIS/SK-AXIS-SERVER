package com.example.skaxis.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.skaxis.interview.model.InterviewInterviewee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InterviewIntervieweeRepository extends JpaRepository<InterviewInterviewee, Long> {
    List<InterviewInterviewee> findByInterview_InterviewId(Long interviewId); // 수정된 부분
    List<InterviewInterviewee> findByInterviewee_IntervieweeId(Long intervieweeId); // 이전에 수정한 부분
    Optional<InterviewInterviewee> findByInterview_InterviewIdAndInterviewee_IntervieweeId(Long interviewId, Long intervieweeId); // 수정된 부분
    
    long countByInterview_InterviewId(Long interviewId); // 수정된 부분

    @Query("SELECT ii FROM InterviewInterviewee ii JOIN FETCH ii.interview JOIN FETCH ii.interviewee")
    List<InterviewInterviewee> findAllWithInterviewAndInterviewee();

    @Query("SELECT ii FROM InterviewInterviewee ii JOIN FETCH ii.interview JOIN FETCH ii.interviewee WHERE ii.interview.interviewId IN :interviewIds")
    List<InterviewInterviewee> findAllWithInterviewAndIntervieweeByInterviewIdIn(@Param("interviewIds") List<Long> interviewIds);
}