package com.ramirezabril.mobility_sharing.converter;

import com.ramirezabril.mobility_sharing.entity.Travel;
import com.ramirezabril.mobility_sharing.model.TravelModel;

public class TravelConverter {

    public static TravelModel toTravelModel(Travel travel) {
        if (travel == null) return null;

        return new TravelModel(
                travel.getId(),
                UserConverter.toUserModel(travel.getDriver()),
                travel.getOrigin(),
                travel.getDestination(),
                travel.getDate(),
                travel.getTime(),
                travel.getPrice(),
                travel.getCreatedAt(),
                (travel.getTravelRecurrence() != null) ?
                        TravelRecurrenceConverter.entityToModel(travel.getTravelRecurrence()) : null
        );
    }

    public static Travel toTravelEntity(TravelModel travelModel) {
        if (travelModel == null) return null;

        Travel travel = new Travel();
        travel.setId(travelModel.getId());
        travel.setDriver(UserConverter.toUserEntity(travelModel.getDriver()));
        travel.setOrigin(travelModel.getOrigin());
        travel.setDestination(travelModel.getDestination());
        travel.setDate(travelModel.getDate());
        travel.setTime(travelModel.getTime());
        travel.setPrice(travelModel.getPrice());
        travel.setCreatedAt(travelModel.getCreatedAt());
        travel.setTravelRecurrence(
                (travelModel.getTravelRecurrenceModel() != null) ?
                        TravelRecurrenceConverter.modelToEntity(travelModel.getTravelRecurrenceModel()) : null // Maneja null
        );

        return travel;
    }
}
