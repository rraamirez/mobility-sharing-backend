package com.ramirezabril.mobility_sharing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private Integer id;
    private String name;
    private String email;
    private String password;
    private String nickname;
    private Integer rupeeWallet = 0;
    private LocalDateTime createdAt;
}
