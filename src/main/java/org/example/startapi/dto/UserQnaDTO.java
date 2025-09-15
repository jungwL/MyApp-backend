package org.example.startapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// com.example.dto.UserQnaDTO.java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserQnaDTO {

    private Long id;
    private String consultType;      // 상담유형 (예: 문의, 칭찬 등)
    private String contentType;      // 내용유형 (예: 제품, 이벤트 등)
    private String title;            // 제목
    private String content;          // 문의 내용
    private String name;             // 작성자 이름
    private String phone;            // 전화번호
    private String email;            // 이메일
    private String addTime;          // 입력 날짜정보
    // 🌟 이 QnA에 달린 답글 목록을 담을 필드 추가
    private List<ReplyDTO> replies;   // 답변 객체
}