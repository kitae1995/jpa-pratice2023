package com.study.jpa.chap05_pratice.repository;

import com.study.jpa.chap05_pratice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
