package com.ramirezabril.mobility_sharing.converter;

import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.model.UserModel;

public class UserConverter {

    public static UserModel toUserModel(User user) {
        return user == null ? null : UserModel.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(null) // Password is never exposed as returned data are always models
                .username(user.getUsername())
                .rupeeWallet(user.getRupeeWallet())
                .createdAt(user.getCreatedAt())
                .role(RoleConverter.toRoleModel(user.getRole()))
                .rating(user.getRating())
                .ecoRank(EcoRankConverter.toModel(user.getEcoRank()))
                .build();
    }

    public static User toUserEntity(UserModel userModel) {
        if (userModel == null) {
            return null;
        }

        return User.builder()
                .id(userModel.getId())
                .name(userModel.getName())
                .email(userModel.getEmail())
                .password(userModel.getPassword())
                .username(userModel.getUsername())
                .rupeeWallet(userModel.getRupeeWallet())
                .createdAt(userModel.getCreatedAt())
                .role(RoleConverter.toRoleEntity(userModel.getRole()))
                .rating(userModel.getRating())
                .ecoRank(EcoRankConverter.toEntity(userModel.getEcoRank()))
                .build();
    }
}
