package org.example.startapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // 이 클래스가 데이터베이스 테이블과 매핑되는 엔티티임을 선언합니다.
@Table(name = "qna") // 실제 데이터베이스의 테이블 이름을 "qna"로 지정합니다.
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserQna {

    @Id // 이 필드가 테이블의 기본 키(Primary Key)임을 나타냅니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값을 데이터베이스가 자동으로 생성(auto-increment)하도록 합니다.
    @Column(name = "id")
    private Long id;

    private String consultType;      // 상담유형
    private String contentType;      // 내용유형
    private String title;            // 제목

    @Lob // 내용이 길어질 수 있으므로 CLOB 또는 TEXT 타입으로 매핑합니다.
    private String content;          // 문의 내용

    private String name;             // 작성자 이름
    private String phone;            // 전화번호
    private String email;            // 이메일

    @Column(nullable = false)
    private LocalDateTime addTime;       // 입력 날짜 (String -> LocalDate 타입으로 변경)
    //  자신에게 달린 Reply 목록을 가질 수 있도록 설정 (1:N 관계)
     /*`cascade = CascadeType.ALL`**:
            * **의미**: "부모(`UserQna`)에게 일어나는 모든(ALL) 일은 자식(`Reply`)에게도 똑같이 적용해라!"
            * **동작**: `UserQna`가 저장(persist)되면 `Reply`도 저장되고, `UserQna`가 삭제(remove)되면 **연관된 모든 `Reply`도 함께 삭제**됩니다. 바로 이 옵션이 질문하신 기능을 활성화합니다.

            * **`orphanRemoval = true`**:
            * **의미**: "부모가 없는 고아(`orphan`) 자식은 제거(removal)해라!"
            * **동작**: `qna.getReplies().remove(0)`처럼 QnA의 답글 목록에서 특정 답글을 제거하면, 그 답글은 더 이상 부모가 없으므로 DB의 `replies` 테이블에서도 자동으로 삭제됩니다. `cascade` 옵션을 보완하는 매우 유용한 기능입니다.*/
    @OneToMany(mappedBy = "userQna", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();


}