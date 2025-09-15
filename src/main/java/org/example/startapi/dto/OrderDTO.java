package org.example.startapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    // --- 주문 기본 정보 ---
    private Long id;
    private String orderNumber;
    private String orderType;
    private String orderName;
    private int quantity;
    private int totalPrice;
    private String orderDate; // JSON 변환 편의를 위해 String으로 전달

    // --- 연관된 정보 ---
    private UserInfo user;        // 주문한 사용자의 정보
    private BreadInfo todayBread; // 주문된 빵의 정보

    /**
     * 주문 정보에 포함될 최소한의 사용자 정보를 담는 내부 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String userName;
    }

    /**
     * 주문 정보에 포함될 최소한의 빵 정보를 담는 내부 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BreadInfo {
        private Long id;
        private String name;
        private String category;
    }
}
