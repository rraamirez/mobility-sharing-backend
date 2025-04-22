package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.model.UserModel;

import java.util.Optional;

public interface UserService {
    Optional<UserModel> getUserByToken(String token);

    void updateRupeeWallet(Integer rupees, Integer userId);

    void computeRupeeWallet(Integer rupees, Integer userId);

    Optional<UserModel> updateUser(UserModel user, String token);

    void deleteUser(Integer userId, String token);
}
