package org.example.startapi.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "replies") // 답변 테이블
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 답변 내용

    @Column(nullable = false)
    private LocalDateTime createdAt; // 답변 등록 시간

    // 어떤 문의(UserQna)에 대한 답변인지 연결 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_id", nullable = false)
    private UserQna userQna;

    // 생성자
    public Reply(String content, UserQna userQna) {
        this.content = content;
        this.userQna = userQna;
        this.createdAt = LocalDateTime.now();
    }
}
