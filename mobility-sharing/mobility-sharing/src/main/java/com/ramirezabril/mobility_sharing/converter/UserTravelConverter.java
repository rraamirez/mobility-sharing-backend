package com.ramirezabril.mobility_sharing.converter;

import com.ramirezabril.mobility_sharing.entity.UserTravel;
import com.ramirezabril.mobility_sharing.model.UserTravelModel;

public class UserTravelConverter {

    public static UserTravelModel toUserTravelModel(UserTravel userTravel) {
        return userTravel == null ? null : new UserTravelModel(
                userTravel.getId(),
                UserConverter.toUserModel(userTravel.getUser()),
                TravelConverter.toTravelModel(userTravel.getTravel()),
                userTravel.getStatus(),
                userTravel.getCreatedAt()
        );
    }

    public static UserTravel toUserTravelEntity(UserTravelModel userTravelModel) {
        if (userTravelModel == null) {
            return null;
        }

        UserTravel userTravel = new UserTravel();
        userTravel.setId(userTravelModel.getId());
        userTravel.setUser(UserConverter.toUserEntity(userTravelModel.getUser()));
        userTravel.setTravel(TravelConverter.toTravelEntity(userTravelModel.getTravel()));
        userTravel.setStatus(userTravelModel.getStatus());
        userTravel.setCreatedAt(userTravelModel.getCreatedAt());

        return userTravel;
    }
}
