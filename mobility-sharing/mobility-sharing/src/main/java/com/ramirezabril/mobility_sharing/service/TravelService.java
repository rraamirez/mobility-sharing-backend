package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.model.TravelModel;
import com.ramirezabril.mobility_sharing.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface TravelService {

    Optional<TravelModel> getTravelById(Integer id);

    List<TravelModel> getAllTravels();

    TravelModel createTravel(TravelModel travelModel, UserModel driver);

    List<TravelModel> createRecurringTravels(List<TravelModel> travelModel, UserModel driver);

    List<List<TravelModel>> getRecurringTravels();

    Optional<TravelModel> updateTravel(TravelModel travelModel, UserModel driver);

    void deleteTravel(Integer id);

    List<TravelModel> getTravelsByDriver(Integer driverId);

    List<TravelModel> getEnrolledTravelsByUser(Integer userId);

    List<List<TravelModel>> getTravelsByOriginAndDestination(String origin, String destination, UserModel userLogged);

    List<TravelModel> getUnratedTravelsByUser(Integer userId);

    Optional<TravelModel> cancelTravel(Integer id, Integer userId);

    Optional<TravelModel> completeTravel(Integer id, Integer userId);
}
