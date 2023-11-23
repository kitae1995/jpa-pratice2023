package com.study.jpa.chap05_pratice.entity;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@ToString(exclude = {"post"}) // 무한반복 막기
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
@Entity
@Table(name = "tbl_hash_tag")
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_no")
    private Long id;


    private String tagName; // 해시태그 이름

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_no")
    private Post post;
}
