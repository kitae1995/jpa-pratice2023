package com.study.jpa.chap01_basic.entity;

import jdk.jfr.Category;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "tbl_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_id")
    private long id;

    @Column(name="prod_name", nullable = false, length = 30)
    private String name;

    private int price;

    @Enumerated(EnumType.STRING) // String을 줘야 enum값(상수값)을 문자로 받음
    private Category category;

    @CreationTimestamp // current_timestamp = 오라클에서의 sysdate
    @Column(updatable = false) // 수정불가
    private LocalDateTime createDate;

    @UpdateTimestamp // 업데이트될때 시간 자동으로
    private LocalDateTime updateDate;

    public enum Category{
        FOOD, FASHION, ELECTRONIC
    }


}
