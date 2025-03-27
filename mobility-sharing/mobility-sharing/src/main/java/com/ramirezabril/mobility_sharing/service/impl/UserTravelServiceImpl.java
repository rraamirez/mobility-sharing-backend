package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.converter.UserTravelConverter;
import com.ramirezabril.mobility_sharing.entity.UserTravel;
import com.ramirezabril.mobility_sharing.model.UserTravelModel;
import com.ramirezabril.mobility_sharing.repository.UserTravelRepository;
import com.ramirezabril.mobility_sharing.service.UserTravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service("userTravelService")
public class UserTravelServiceImpl implements UserTravelService {
    @Autowired
    private UserTravelRepository userTravelRepository;

    @Override
    public Optional<UserTravelModel> addUserTravel(UserTravelModel userTravelModel) {
        var userId = userTravelModel.getUser().getId();
        var travelId = userTravelModel.getTravel().getId();
        var userNotEnrolled = userTravelRepository
                .findUserAndTravel(userId, travelId).isEmpty();

        if (userNotEnrolled) {
            UserTravel savedEntity = userTravelRepository.save(UserTravelConverter.toUserTravelEntity(userTravelModel));
            return Optional.of(UserTravelConverter.toUserTravelModel(savedEntity));
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already enrolled in this travel");
        }
    }


    @Override
    public Optional<UserTravelModel> updateUserTravel(UserTravelModel userTravelModel) {
        return userTravelRepository.findById(userTravelModel.getId())
                .map(existingEntity -> {
                    existingEntity.setId(userTravelModel.getId());
                    UserTravel updatedEntity = userTravelRepository.save(existingEntity);
                    return UserTravelConverter.toUserTravelModel(updatedEntity);
                });
    }


    @Override
    public void deleteUserTravel(UserTravelModel userTravelModel) {
        if (userTravelRepository.existsById(userTravelModel.getId())) {
            userTravelRepository.deleteById(userTravelModel.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UserTravel not found");
        }
    }


    @Override
    public List<UserTravelModel> getAllUserTravels() {
        return userTravelRepository.findAll().stream().map(UserTravelConverter::toUserTravelModel).toList();
    }

    @Override
    public Optional<UserTravelModel> getUserTravel(int userTravelId) {
        return userTravelRepository.findById(userTravelId)
                .map(UserTravelConverter::toUserTravelModel);
    }


    @Override
    public List<UserTravelModel> getUserTravelsByUser(int userId) {
        return userTravelRepository.findByUser_Id(userId).stream().map(UserTravelConverter::toUserTravelModel).toList();
    }

    @Override
    public List<UserTravelModel> getUserTravelsByTravelId(int userId) {
        return userTravelRepository.findByTravel_Id(userId).stream().map(UserTravelConverter::toUserTravelModel).toList();
    }
}
