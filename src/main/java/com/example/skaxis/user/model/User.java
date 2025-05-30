package com.example.skaxis.user.model;

import com.example.skaxis.user.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;
    
    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;
    
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}