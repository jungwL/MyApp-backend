package org.example.startapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @JsonProperty("userId")
    private String userId;  //아이디(이메일)

    @JsonProperty("password")
    private String password; //비밀번호

    private String userName; //이름

    private int userPoint;  //멤버십 포인트 점수q

    private String phoneNumber; //휴대폰 번호

    private int pinNo; // 핀번호

    private String userAddress;

}