package com.example.skaxis.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.skaxis.interview.model.InterviewInterviewee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InterviewIntervieweeRepository extends JpaRepository<InterviewInterviewee, Long> {
    List<InterviewInterviewee> findByInterviewId(Long interviewId);
    List<InterviewInterviewee> findByIntervieweeId(Long intervieweeId);
    Optional<InterviewInterviewee> findByInterviewIdAndIntervieweeId(Long interviewId, Long intervieweeId);

    long countByInterviewId(Long interviewId);

    @Query("SELECT ii FROM InterviewInterviewee ii JOIN FETCH ii.interview JOIN FETCH ii.interviewee")
    List<InterviewInterviewee> findAllWithInterviewAndInterviewee();

    @Query("SELECT ii FROM InterviewInterviewee ii JOIN FETCH ii.interview JOIN FETCH ii.interviewee WHERE ii.interview.interviewId IN :interviewIds")
    List<InterviewInterviewee> findAllWithInterviewAndIntervieweeByInterviewIdIn(@Param("interviewIds") List<Long> interviewIds);
}