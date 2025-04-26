package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.converter.TravelConverter;
import com.ramirezabril.mobility_sharing.converter.UserConverter;
import com.ramirezabril.mobility_sharing.converter.UserTravelConverter;
import com.ramirezabril.mobility_sharing.entity.UserTravel;
import com.ramirezabril.mobility_sharing.model.UserTravelModel;
import com.ramirezabril.mobility_sharing.repository.TravelRepository;
import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.repository.UserTravelRepository;
import com.ramirezabril.mobility_sharing.service.UserService;
import com.ramirezabril.mobility_sharing.service.UserTravelService;
import com.ramirezabril.mobility_sharing.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service("userTravelService")
public class UserTravelServiceImpl implements UserTravelService {
    @Autowired
    private UserTravelRepository userTravelRepository;

    @Autowired
    private TravelRepository travelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public Optional<UserTravelModel> addUserTravel(UserTravelModel userTravelModel) {
        var userId = userTravelModel.getUser().getId();
        var travelId = userTravelModel.getTravel().getId();
        var userToAdd = userRepository.findById(userId).map(UserConverter::toUserModel).orElse(null);
        var joiningTravel = travelRepository.findById(travelId).map(TravelConverter::toTravelModel).orElse(null);

        if (userToAdd == null || joiningTravel == null) {
            throw new RuntimeException("There was an error matching user and joining travel");
        }

        if (userToAdd.getRupeeWallet() < joiningTravel.getPrice()) {
            throw new RuntimeException("Joining user has not enough rupee wallet for joining travel");
        }

        var userNotEnrolled = userTravelRepository
                .findUserAndTravel(userId, travelId).isEmpty();

        if (userNotEnrolled) {
            userTravelModel.setStatus(Status.pending);
            UserTravel savedEntity = userTravelRepository.save(UserTravelConverter.toUserTravelEntity(userTravelModel));
            userService.computeRupeeWallet(-(savedEntity.getTravel().getPrice()), savedEntity.getUser().getId());

            return Optional.of(UserTravelConverter.toUserTravelModel(savedEntity));
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already enrolled in this travel");
        }
    }

    public Optional<UserTravelModel> updateUserTravelStatus(Integer userTravelId, Status status) {
        var userTravel = userTravelRepository.findById(userTravelId)
                .map(UserTravelConverter::toUserTravelModel)
                .orElse(null);

        if (userTravel == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User travel not found");
        } else {
            userTravel.setStatus(status);
            var updatedEntity = userTravelRepository.save(UserTravelConverter.toUserTravelEntity(userTravel));
            return Optional.of(UserTravelConverter.toUserTravelModel(updatedEntity));
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
    public Optional<UserTravelModel> rejectUserTravel(Integer travelId, Integer userId) {
        var userTravel = userTravelRepository.findConcreteUserTravel(userId, travelId);
        if (userTravel.isPresent()) {
            var rejected = userTravel.get();
            rejected.setStatus(Status.canceled);
            var toReturn = userTravelRepository.save(rejected);
            return Optional.of(UserTravelConverter.toUserTravelModel(toReturn));
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserTravelModel> acceptUserTravel(Integer travelId, Integer userId) {
        var userTravel = userTravelRepository.findConcreteUserTravel(userId, travelId);
        if (userTravel.isPresent()) {
            var acceptedTravel = userTravel.get();
            acceptedTravel.setStatus(Status.confirmed);
            var toReturn = userTravelRepository.save(acceptedTravel);
            return Optional.of(UserTravelConverter.toUserTravelModel(toReturn));
        }
        return Optional.empty();
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
