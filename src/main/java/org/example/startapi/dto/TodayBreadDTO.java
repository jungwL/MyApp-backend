package org.example.startapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodayBreadDTO {

    private Long id;
    private String name;
    private String image;
    private String ingredients;
    private int price;
    private String category;
    private double avgRating; // 평균 평점
}
