package org.example.startapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "admin")
public class Admin  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String adminName;
    @Column(unique = true, nullable = false)
    private String adminId; // 관리자 ID
    private String adminPassword; //관리자 PW
    private String adminPhoneNumber; //관리자 휴대폰번호

}
