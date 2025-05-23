package com.ramirezabril.mobility_sharing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {

    private Integer id;
    private String name;
    private String email;
    private String password;
    private String username;
    private Integer rupeeWallet = 0;
    private LocalDateTime createdAt;
    private RoleModel role;
    private Integer rating;
    private EcoRankModel ecoRank;
}
