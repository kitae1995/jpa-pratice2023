package com.study.jpa.chap01_basic.repository;

import com.study.jpa.chap01_basic.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // jpa로 쓸 엔터티테이블 (Product)  그리고 PK의 데이터형태 ( Long 으로 설정했었음 )
}
