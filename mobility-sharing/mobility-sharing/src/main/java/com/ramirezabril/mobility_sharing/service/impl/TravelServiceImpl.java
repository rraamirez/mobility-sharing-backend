package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.converter.TravelConverter;
import com.ramirezabril.mobility_sharing.converter.TravelRecurrenceConverter;
import com.ramirezabril.mobility_sharing.entity.Travel;
import com.ramirezabril.mobility_sharing.entity.TravelRecurrence;
import com.ramirezabril.mobility_sharing.model.TravelModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.repository.TravelRecurrenceRepository;
import com.ramirezabril.mobility_sharing.repository.TravelRepository;
import com.ramirezabril.mobility_sharing.service.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("travelService")
public class TravelServiceImpl implements TravelService {
    @Autowired
    private TravelRepository travelRepository;

    @Autowired
    private TravelRecurrenceRepository travelRecurrenceRepository;

    @Override
    public Optional<TravelModel> getTravelById(Integer id) {
        return travelRepository.findById(id).map(TravelConverter::toTravelModel);
    }

    @Override
    public List<TravelModel> getAllTravels() {
        return travelRepository.findAll().stream().map(TravelConverter::toTravelModel).toList();
    }

    @Override
    public TravelModel createTravel(TravelModel travelModel, UserModel driver) {
        travelModel.setDriver(driver);
        var savedTravel = travelRepository.save(TravelConverter.toTravelEntity(travelModel));
        return TravelConverter.toTravelModel(savedTravel);
    }

    @Override
    public List<TravelModel> createRecurringTravels(List<TravelModel> travelModels, UserModel driver) {
        var travelRecurrence = travelRecurrenceRepository.save(new TravelRecurrence());

        travelModels.forEach(travelModel -> {
            travelModel.setDriver(driver);
            travelModel.setTravelRecurrenceModel(TravelRecurrenceConverter.entityToModel(travelRecurrence));
        });

        List<Travel> travelEntities = travelModels.stream()
                .map(TravelConverter::toTravelEntity)
                .toList();

        List<Travel> savedTravels = travelRepository.saveAll(travelEntities);

        return savedTravels.stream()
                .map(TravelConverter::toTravelModel)
                .toList();
    }

    @Override
    public List<List<TravelModel>> getRecurringTravels() {
        var allRecurringTravels = travelRepository.getRecurringTravels().stream()
                .map(TravelConverter::toTravelModel)
                .toList();

        Map<Integer, List<TravelModel>> recurringTravels = allRecurringTravels.stream()
                .collect(Collectors.groupingBy(travel -> travel.getTravelRecurrenceModel() != null
                        ? travel.getTravelRecurrenceModel().getId() : null));

        return recurringTravels.values().stream().toList();
    }


    @Override
    public Optional<TravelModel> updateTravel(TravelModel travelModel, UserModel driver) {
        return travelRepository.findById(travelModel.getId())
                .map(existingTravel -> {
                    travelModel.setDriver(driver);
                    var updatedTravel = TravelConverter.toTravelEntity(travelModel);
                    updatedTravel.setId(travelModel.getId());
                    var savedTravel = travelRepository.save(updatedTravel);
                    return TravelConverter.toTravelModel(savedTravel);
                });
    }


    @Override
    public void deleteTravel(Integer id) {
        travelRepository.deleteById(id);
    }

    @Override
    public List<TravelModel> getTravelsByDriver(Integer driverId) {
        return travelRepository.findByDriverId(driverId).stream().map(TravelConverter::toTravelModel).toList();
    }

    @Override
    public List<TravelModel> getEnrolledTravelsByUser(Integer userId) {
        return travelRepository.findEnrolledTravelsByUserId(userId).stream().map(TravelConverter::toTravelModel).toList();
    }

    @Override
    public List<TravelModel> getTravelsByOriginAndDestination(String origin, String destination, UserModel userLogged) {
        return travelRepository.findByOriginAndDestination(origin, destination, userLogged.getId()).stream()
                .map(TravelConverter::toTravelModel).toList();
    }

    @Override
    public List<TravelModel> getUnratedTravelsByUser(Integer userId) {
        return travelRepository.findUnratedTravelsByUserId(userId).stream().map(TravelConverter::toTravelModel).toList();
    }
}
