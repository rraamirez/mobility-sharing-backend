package com.ramirezabril.mobility_sharing.converter;

import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.model.UserModel;

public class UserConverter {

    public static UserModel toUserModel(User user) {
        return user == null ? null : new UserModel(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getRupeeWallet(),
                user.getCreatedAt()
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
        user.setNickname(userModel.getNickname());
        user.setRupeeWallet(userModel.getRupeeWallet());
        user.setCreatedAt(userModel.getCreatedAt());

        return user;
    }
}
