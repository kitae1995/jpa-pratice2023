package com.study.jpa.chap05_pratice.repository;

import com.study.jpa.chap05_pratice.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashTagRepository
        extends JpaRepository<HashTag, Long> {

}
