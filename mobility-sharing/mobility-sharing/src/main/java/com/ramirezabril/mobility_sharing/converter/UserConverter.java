package com.ramirezabril.mobility_sharing.converter;

import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.model.UserModel;

public class UserConverter {

    public static UserModel toUserModel(User user) {
        return user == null ? null : new UserModel(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null,
                user.getUsername(),
                user.getRupeeWallet(),
                user.getCreatedAt(),
                RoleConverter.toRoleModel(user.getRole()),
                user.getRating()
        );
    }

    public static User toUserEntity(UserModel userModel) {
        if (userModel == null) {
            return null;
        }

        User user = new User();
        user.setId(userModel.getId());
        user.setName(userModel.getName());
        user.setEmail(userModel.getEmail());
        user.setPassword(userModel.getPassword());
        user.setUsername(userModel.getUsername());
        user.setRupeeWallet(userModel.getRupeeWallet());
        user.setCreatedAt(userModel.getCreatedAt());
        user.setRole(RoleConverter.toRoleEntity(userModel.getRole()));
        user.setRating(userModel.getRating());
        
        return user;
    }
}
