package com.port4dev.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;    // 아이디

    private String password;    // 비밀번호
    
    private String name;        // 이름
    
    @Column(unique = true)
    private String email;       // 이메일
    
    private String phone;       // 전화번호
} 