package org.example.startapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문 번호 (예: 20250911-0001)
    @Column(unique = true, nullable = false)
    private String orderNumber;

    // 주문 유형 (예: '매장', '배달')
    private String orderType;

    // 주문자 이름 (User 엔티티에서 가져올 수도 있지만, 비회원 주문 등을 위해 별도 저장)
    private String orderName;

    // 주문 수량
    private int quantity;

    // 총 주문 금액
    private int totalPrice;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    // --- 관계 매핑 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bread_id")
    private TodayBread todayBread;
}


