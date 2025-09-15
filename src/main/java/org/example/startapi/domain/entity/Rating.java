package org.example.startapi.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ratingsbread") // 실제 DB 테이블 이름
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double rating; // 1.0 ~ 5.0 사이의 값

    // 여러 개의 평점은 하나의 빵에 속함 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todaybreadlist_id", nullable = false) // 외래키
    private TodayBread todayBread;

}
