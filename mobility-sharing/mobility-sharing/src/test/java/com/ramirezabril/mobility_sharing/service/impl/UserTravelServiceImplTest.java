package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.entity.Travel;
import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.entity.UserTravel;
import com.ramirezabril.mobility_sharing.model.UserTravelModel;
import com.ramirezabril.mobility_sharing.repository.TravelRepository;
import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.repository.UserTravelRepository;
import com.ramirezabril.mobility_sharing.service.UserService;
import com.ramirezabril.mobility_sharing.util.EnvironmentalActionLevel;
import com.ramirezabril.mobility_sharing.util.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTravelServiceImplTest {

    @Mock
    private UserTravelRepository userTravelRepository;

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserTravelServiceImpl userTravelService;


    @Test
    void updateUserTravelStatus_notFound() {
        when(userTravelRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userTravelService.updateUserTravelStatus(1, Status.confirmed));
    }

    @Test
    void updateUserTravel_success() {
        UserTravelModel userTravelModel = new UserTravelModel();
        userTravelModel.setId(1);

        UserTravel userTravel = new UserTravel();
        userTravel.setId(1);

        when(userTravelRepository.findById(1)).thenReturn(Optional.of(userTravel));
        when(userTravelRepository.save(any(UserTravel.class))).thenReturn(userTravel);

        Optional<UserTravelModel> result = userTravelService.updateUserTravel(userTravelModel);

        assertTrue(result.isPresent());
    }

    @Test
    void rejectUserTravel_success() {
        UserTravel userTravel = new UserTravel();
        userTravel.setId(1);
        userTravel.setStatus(Status.pending);

        when(userTravelRepository.findConcreteUserTravel(1, 1)).thenReturn(Optional.of(userTravel));
        when(userTravelRepository.save(any(UserTravel.class))).thenReturn(userTravel);

        Optional<UserTravelModel> result = userTravelService.rejectUserTravel(1, 1);

        assertTrue(result.isPresent());
        assertEquals(Status.canceled, result.get().getStatus());
    }

    @Test
    void rejectUserTravel_alreadyCanceled() {
        UserTravel userTravel = new UserTravel();
        userTravel.setId(1);
        userTravel.setStatus(Status.canceled);

        when(userTravelRepository.findConcreteUserTravel(1, 1)).thenReturn(Optional.of(userTravel));

        assertThrows(ResponseStatusException.class, () -> userTravelService.rejectUserTravel(1, 1));
    }

    @Test
    void rejectUserTravel_notFound() {
        when(userTravelRepository.findConcreteUserTravel(1, 1)).thenReturn(Optional.empty());

        Optional<UserTravelModel> result = userTravelService.rejectUserTravel(1, 1);

        assertTrue(result.isEmpty());
    }

    @Test
    void acceptUserTravel_success() {
        User driver = new User();
        driver.setId(2);

        Travel travel = new Travel();
        travel.setId(1);
        travel.setPrice(50);
        travel.setDriver(driver);

        UserTravel userTravel = new UserTravel();
        userTravel.setId(1);
        userTravel.setStatus(Status.pending);
        userTravel.setTravel(travel);

        when(userTravelRepository.findConcreteUserTravel(1, 1)).thenReturn(Optional.of(userTravel));
        when(userTravelRepository.countByTravelIdAndCompleted(1)).thenReturn(Optional.of(0L));
        when(userTravelRepository.save(any(UserTravel.class))).thenReturn(userTravel);

        Optional<UserTravelModel> result = userTravelService.acceptUserTravel(1, 1);

        assertTrue(result.isPresent());
        assertEquals(Status.confirmed, result.get().getStatus());
        verify(userService).computeRupeeWallet(-50, 1);
        verify(userService).computeRupeeWallet(50, 2);
        verify(travelRepository).updateEnvironmentalActionLevel(1, EnvironmentalActionLevel.HIGH);
    }

    @Test
    void acceptUserTravel_alreadyConfirmed() {
        UserTravel userTravel = new UserTravel();
        userTravel.setId(1);
        userTravel.setStatus(Status.confirmed);

        when(userTravelRepository.findConcreteUserTravel(1, 1)).thenReturn(Optional.of(userTravel));

        assertThrows(ResponseStatusException.class, () -> userTravelService.acceptUserTravel(1, 1));
    }

    @Test
    void acceptUserTravel_notFound() {
        when(userTravelRepository.findConcreteUserTravel(1, 1)).thenReturn(Optional.empty());

        Optional<UserTravelModel> result = userTravelService.acceptUserTravel(1, 1);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUserTravel_success() {
        UserTravelModel userTravelModel = new UserTravelModel();
        userTravelModel.setId(1);

        when(userTravelRepository.existsById(1)).thenReturn(true);

        userTravelService.deleteUserTravel(userTravelModel);

        verify(userTravelRepository).deleteById(1);
    }

    @Test
    void deleteUserTravel_notFound() {
        UserTravelModel userTravelModel = new UserTravelModel();
        userTravelModel.setId(1);

        when(userTravelRepository.existsById(1)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> userTravelService.deleteUserTravel(userTravelModel));
    }

    @Test
    void getAllUserTravels_success() {
        when(userTravelRepository.findAll()).thenReturn(List.of(new UserTravel()));

        List<UserTravelModel> result = userTravelService.getAllUserTravels();

        assertFalse(result.isEmpty());
    }

    @Test
    void getUserTravel_success() {
        when(userTravelRepository.findById(1)).thenReturn(Optional.of(new UserTravel()));

        Optional<UserTravelModel> result = userTravelService.getUserTravel(1);

        assertTrue(result.isPresent());
    }

    @Test
    void getUserTravelsByUser_success() {
        when(userTravelRepository.findByUser_Id(1)).thenReturn(List.of(new UserTravel()));

        List<UserTravelModel> result = userTravelService.getUserTravelsByUser(1);

        assertFalse(result.isEmpty());
    }

    @Test
    void getUserTravelsByTravelId_success() {
        when(userTravelRepository.findByTravel_Id(1)).thenReturn(List.of(new UserTravel()));

        List<UserTravelModel> result = userTravelService.getUserTravelsByTravelId(1);

        assertFalse(result.isEmpty());
    }

    @Test
    void getUserTravelByUserIdAndTravelId_success() {
        when(userTravelRepository.findConcreteUserTravel(1, 1)).thenReturn(Optional.of(new UserTravel()));

        Optional<UserTravelModel> result = userTravelService.getUserTravelByUserIdAndTravelId(1, 1);

        assertTrue(result.isPresent());
    }
}