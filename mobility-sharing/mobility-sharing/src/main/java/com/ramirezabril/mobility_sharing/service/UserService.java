package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.model.WeeklyEnvironmentalStatsDTO;

import java.util.Optional;

public interface UserService {
    Optional<UserModel> getUserByToken(String token);

    void updateRupeeWallet(Integer rupees, Integer userId);

    void computeRupeeWallet(Integer rupees, Integer userId);

    Optional<UserModel> updateUser(UserModel user, String token);

    void deleteUser(Integer userId, String token);

    void computeUserRatings();

    void computeEcoRanks();

    int calculateWeeklyRupees(Integer userId);

    WeeklyEnvironmentalStatsDTO getWeeklyEnvironmentalStats(Integer userId);
}
