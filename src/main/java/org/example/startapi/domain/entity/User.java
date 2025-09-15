package org.example.startapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users") // 데이터베이스 테이블 이름을 "users"로 지정
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 엔티티의 고유 식별자 (Primary Key)

    @Column(unique = true, nullable = false)
    private String userId;  // 아이디(이메일)

    @Column(nullable = false)
    private String password; // 비밀번호

    private String userName; // 이름

    private int userPoint;  // 멤버십 포인트 점수

    @Column(unique = true, nullable = false)
    private String phoneNumber; // 휴대폰 번호

    private int pinNo; // 핀번호

    private String userAddress; // 주소

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

}