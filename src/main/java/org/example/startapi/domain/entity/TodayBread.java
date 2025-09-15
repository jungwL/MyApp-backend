package org.example.startapi.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "todaybreadlist") // 실제 DB 테이블 이름
public class TodayBread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String image;

    @Column(columnDefinition = "TEXT") // 긴 텍스트를 위해
    private String ingredients;

    private int price;

    @Column(nullable = false)
    private String category;

    // 하나의 빵은 여러 개의 평점을 가질 수 있음 (1:N 관계)
    @OneToMany(mappedBy = "todayBread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "todayBread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
}
