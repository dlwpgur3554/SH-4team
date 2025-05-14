package com.port4dev.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Portfolio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer age;
    private String edu;
    private String career;
    private String stack;
    private String selfIntro;
    private String contact;
    // 필요시 어학점수, 자격증, 프로젝트 등은 별도 Entity/JSON 컬럼으로 확장 가능
}