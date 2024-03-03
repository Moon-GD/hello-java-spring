package com.hellospring.ex2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // Spring Data JPA에게 해당 클래스가 엔티티 클래스임을 알려주는 것
@Table(name="tbl_memo")  // DB 테이블과 관련된 설정 (index, schema 등도 지정 가능)
@ToString  // lombok에서 자동으로 생성해주는 toString 메서드 활용을 위함
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Memo {

    @Id  // Entity이기 때문에 필요한 Primary key를 명시
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // primary key 생성 전략
    private Long mno;

    @Column(length = 200, nullable = false)
    private String memoText;
}
