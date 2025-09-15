package org.example.startapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {

    private Long id;

    private String content;

    private String createdAt; // JSON으로 변환하기 쉽도록 String 타입으로 선언

    private Long qnaId; // 이 답글이 속한 QnA의 ID

}
