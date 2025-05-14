package com.port4dev.repository;

import com.port4dev.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    void deleteByAuthor(String author);
} 