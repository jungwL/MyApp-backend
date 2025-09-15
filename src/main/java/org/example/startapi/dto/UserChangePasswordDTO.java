package org.example.startapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChangePasswordDTO {
    private String phoneNumber; // 전화번호
    private String newPassword; //신규 비밀번호

}
