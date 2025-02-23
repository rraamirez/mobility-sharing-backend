package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.model.UserTravelModel;

import java.util.List;
import java.util.Optional;

public interface UserTravelService {
    Optional<UserTravelModel> addUserTravel(UserTravelModel userTravelModel);

    Optional<UserTravelModel> updateUserTravel(UserTravelModel userTravelModel);

    void deleteUserTravel(UserTravelModel userTravelModel);

    List<UserTravelModel> getAllUserTravels();

    Optional<UserTravelModel> getUserTravel(int userTravelId);

    List<UserTravelModel> getUserTravelsByUser(int userId);

    List<UserTravelModel> getUserTravelsByTravelId(int userId);

}
