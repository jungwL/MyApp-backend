package org.example.startapi.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdminDTO {

    private String adminName;
    private String adminPassword;
    private String adminId;
    private String adminPhoneNumber;

}
