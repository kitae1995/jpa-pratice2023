package com.study.jpa.chap04_relation.entity;

import lombok.*;

import javax.persistence.*;
@Setter @Getter
//jpa 연관 관계 매핑에서 연관관계 데이터는 toString에서 제외해야 합니다.
@ToString(exclude = "department") // 객체 정보를 불러올때 department는 빼달라는 뚯
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity // 테이블
@Table(name = "tbl_emp")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long id;

    @Column(name = "emp_name", nullable = false)
    private String name;

    // EAGER : 항상 무조건 조인을 수행
    // LAZY : 필요한 경우에만 조인을 수행
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id") // FK를 알려줌 (join)
    private Department department;


}
