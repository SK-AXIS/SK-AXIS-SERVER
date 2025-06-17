package com.example.skaxis.question.model;

import com.example.skaxis.interview.model.Interview;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;
    
    @Column(name = "interview_id", nullable = false)
    private Long interviewId;
    
    @Column(name = "type", nullable = false, length = 20)
    private String type;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    // 면접과의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", insertable = false, updatable = false)
    @JsonIgnore
    private Interview interview;
}