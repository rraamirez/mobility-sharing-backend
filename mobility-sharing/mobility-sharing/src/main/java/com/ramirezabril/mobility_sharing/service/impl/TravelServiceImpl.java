package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.converter.TravelConverter;
import com.ramirezabril.mobility_sharing.converter.TravelRecurrenceConverter;
import com.ramirezabril.mobility_sharing.entity.TravelRecurrence;
import com.ramirezabril.mobility_sharing.model.TravelModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.repository.TravelRecurrenceRepository;
import com.ramirezabril.mobility_sharing.repository.TravelRepository;
import com.ramirezabril.mobility_sharing.repository.UserTravelRepository;
import com.ramirezabril.mobility_sharing.service.TravelService;
import com.ramirezabril.mobility_sharing.util.EnvironmentalActionLevel;
import com.ramirezabril.mobility_sharing.util.TravelStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("travelService")
public class TravelServiceImpl implements TravelService {
    @Autowired
    private TravelRepository travelRepository;

    @Autowired
    private TravelRecurrenceRepository travelRecurrenceRepository;

    @Autowired
    private UserTravelRepository userTravelRepository;

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
        travelModel.setStatus(TravelStatus.ACTIVE);
        travelModel.setEnvironmentalActionLevel(EnvironmentalActionLevel.HIGH);

        var savedTravel = travelRepository.save(TravelConverter.toTravelEntity(travelModel));
        return TravelConverter.toTravelModel(savedTravel);
    }

    @Override
    public List<TravelModel> createRecurringTravels(List<TravelModel> travelModels, UserModel driver) {
        var travelRecurrence = travelRecurrenceRepository.save(new TravelRecurrence());

        travelModels.forEach(travelModel -> {
            travelModel.setDriver(driver);
            travelModel.setStatus(TravelStatus.ACTIVE);
            travelModel.setTravelRecurrenceModel(
                    TravelRecurrenceConverter.entityToModel(travelRecurrence)
            );
            travelModel.setEnvironmentalActionLevel(EnvironmentalActionLevel.HIGH);
        });

        var travelEntities = travelModels.stream()
                .map(TravelConverter::toTravelEntity)
                .toList();

        var savedTravels = travelRepository.saveAll(travelEntities);

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
    @Transactional
    public void deleteTravel(Integer id) {
        if (travelRepository.existsById(id)) {
            userTravelRepository.deleteByTravelId(id); //if travel is deleted, all users enrolled to this travel must be removed (the register)
            travelRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Travel with ID " + id + " not found");
        }
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
    public List<List<TravelModel>> getTravelsByOriginAndDestination(String origin, String destination, UserModel userLogged) {
        List<TravelModel> flat = travelRepository
                .findByOriginAndDestination(origin, destination, userLogged.getId())
                .stream()
                .map(TravelConverter::toTravelModel)
                .toList();

        Map<Integer, List<TravelModel>> mapped = new HashMap<>();
        for (TravelModel travel : flat) {
            Integer key = travel.getTravelRecurrenceModel() != null
                    ? travel.getTravelRecurrenceModel().getId()
                    : 0;
            mapped
                    .computeIfAbsent(key, k -> new ArrayList<>())
                    .add(travel);
        }

        List<TravelModel> nonRecurringTravels = new ArrayList<>();
        if (mapped.containsKey(0) && mapped.get(0).isEmpty()) {
            mapped.remove(0);
        } else if (mapped.containsKey(0) && !mapped.get(0).isEmpty()) {
            nonRecurringTravels = mapped.get(0);
            mapped.remove(0);
        }

        List<List<TravelModel>> result = new ArrayList<>();

        if (!nonRecurringTravels.isEmpty()) {
            for (TravelModel travel : nonRecurringTravels) {
                result.add(List.of(travel));
            }
        }


        for (Integer key : mapped.keySet()) {
            result.add(mapped.get(key));
        }

        return result;
    }

    @Override
    public List<TravelModel> getUnratedTravelsByUser(Integer userId) {
        return travelRepository.findUnratedTravelsByUserId(userId).stream().map(TravelConverter::toTravelModel).toList();
    }

    public Optional<TravelModel> updateTravelStatus(Integer id, Integer userId, TravelStatus status, String errorMessage) {
        var travel = travelRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Travel with ID " + id + " not found"));

        if (!travel.getDriver().getId().equals(userId)) {
            throw new RuntimeException(errorMessage);
        }

        travel.setStatus(status);
        var travelToReturn = travelRepository.save(travel);
        return Optional.of(TravelConverter.toTravelModel(travelToReturn));
    }

    @Override
    public Optional<TravelModel> cancelTravel(Integer id, Integer userId) {
        return updateTravelStatus(id, userId, TravelStatus.CANCELED, "Can't cancel travel, users do not match");
    }

    @Override
    public Optional<TravelModel> completeTravel(Integer id, Integer userId) {
        return updateTravelStatus(id, userId, TravelStatus.COMPLETED, "Can't complete travel, users do not match");
    }

}
