package com.port4dev.repository;

import com.port4dev.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Member findByUsername(String username);
} 