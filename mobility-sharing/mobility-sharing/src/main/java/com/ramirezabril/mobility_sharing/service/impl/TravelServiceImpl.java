package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.converter.TravelConverter;
import com.ramirezabril.mobility_sharing.model.TravelModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.repository.TravelRepository;
import com.ramirezabril.mobility_sharing.service.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("travelService")
public class TravelServiceImpl implements TravelService {
    @Autowired
    private TravelRepository travelRepository;

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
    public List<TravelModel> getTravelsByOriginAndDestination(String origin, String destination) {
        return travelRepository.findByOriginAndDestination(origin, destination).stream()
                .map(TravelConverter::toTravelModel).toList();
    }
}
