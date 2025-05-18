package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.converter.TravelConverter;
import com.ramirezabril.mobility_sharing.converter.UserConverter;
import com.ramirezabril.mobility_sharing.converter.UserTravelConverter;
import com.ramirezabril.mobility_sharing.entity.UserTravel;
import com.ramirezabril.mobility_sharing.model.TravelModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.model.UserTravelModel;
import com.ramirezabril.mobility_sharing.repository.TravelRepository;
import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.repository.UserTravelRepository;
import com.ramirezabril.mobility_sharing.service.impl.UserServiceImpl;
import com.ramirezabril.mobility_sharing.service.impl.UserTravelServiceImpl;
import com.ramirezabril.mobility_sharing.util.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTravelServiceTest {

    @Mock
    private UserTravelRepository userTravelRepository;

    @Mock
    private UserTravelConverter userTravelConverter;

    @Mock UserRepository userRepository;
    @Mock TravelRepository travelRepository;

    @InjectMocks
    private UserTravelServiceImpl userTravelService;

    @Mock
    private UserServiceImpl userService;

    private UserTravelModel userTravelModel;
    private UserTravel userTravelEntity;
    private UserTravel userTravelEntity2;
    private UserModel userModel;
    private TravelModel travelModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userModel = new UserModel();
        userModel.setId(1);
        userModel.setUsername("testuser");
        userModel.setRupeeWallet(200);

        travelModel = new TravelModel();
        travelModel.setId(1);
        travelModel.setDestination("Destination");
        travelModel.setPrice(100);

        userTravelModel = new UserTravelModel();
        userTravelModel.setId(1);
        userTravelModel.setUser(userModel);
        userTravelModel.setTravel(travelModel);
        userTravelModel.setStatus(Status.pending);
        userTravelModel.setCreatedAt(LocalDateTime.now());

        userTravelEntity = new UserTravel();
        userTravelEntity.setId(1);
        userTravelEntity.setUser(UserConverter.toUserEntity(userModel));
        userTravelEntity.setTravel(TravelConverter.toTravelEntity(travelModel));
        userTravelEntity.setStatus(Status.pending);
        userTravelEntity.setCreatedAt(LocalDateTime.now());

        userTravelEntity2 = new UserTravel();
        userTravelEntity2.setId(2);
        userTravelEntity2.setUser(UserConverter.toUserEntity(userModel));
        userTravelEntity2.setTravel(TravelConverter.toTravelEntity(travelModel));
        userTravelEntity2.setStatus(Status.pending);
        userTravelEntity2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testAddUserTravel() {
        when(userTravelRepository.save(any(UserTravel.class))).thenReturn(userTravelEntity);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(UserConverter.toUserEntity(userModel)));
        when(travelRepository.findById(anyInt())).thenReturn(Optional.of(TravelConverter.toTravelEntity(travelModel)));
        doNothing().when(userService).computeRupeeWallet(anyInt(), anyInt());


        Optional<UserTravelModel> result = userTravelService.addUserTravel(userTravelModel);

        assertTrue(result.isPresent());
        assertEquals(userTravelModel.getId(), result.get().getId());
        assertEquals(userTravelModel.getUser().getId(), result.get().getUser().getId());
        assertEquals(userTravelModel.getTravel().getId(), result.get().getTravel().getId());
        verify(userTravelRepository, times(1)).save(any(UserTravel.class));
    }

    @Test
    void testUpdateUserTravel() {
        when(userTravelRepository.findById(anyInt())).thenReturn(Optional.of(userTravelEntity));
        when(userTravelRepository.save(any(UserTravel.class))).thenReturn(userTravelEntity);

        Optional<UserTravelModel> result = userTravelService.updateUserTravel(userTravelModel);

        assertTrue(result.isPresent());
        assertEquals(userTravelModel.getId(), result.get().getId());
        assertEquals(userTravelModel.getUser().getId(), result.get().getUser().getId());
        assertEquals(userTravelModel.getTravel().getId(), result.get().getTravel().getId());
        verify(userTravelRepository, times(1)).findById(anyInt());
        verify(userTravelRepository, times(1)).save(any(UserTravel.class));
    }

    @Test
    void testDeleteUserTravel() {
        when(userTravelRepository.existsById(anyInt())).thenReturn(true);

        userTravelService.deleteUserTravel(userTravelModel);

        verify(userTravelRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testDeleteUserTravelNotFound() {
        when(userTravelRepository.existsById(anyInt())).thenReturn(false);

        // Act and Assert
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            userTravelService.deleteUserTravel(userTravelModel);
        });
        assertEquals("404 NOT_FOUND \"UserTravel not found\"", exception.getMessage());
    }

    @Test
    void testGetAllUserTravels() {
        when(userTravelRepository.findAll()).thenReturn(List.of(userTravelEntity));

        List<UserTravelModel> result = userTravelService.getAllUserTravels();

        assertFalse(result.isEmpty());
        assertEquals(userTravelModel.getId(), result.get(0).getId());
    }

    @Test
    void testGetUserTravel() {
        when(userTravelRepository.findById(anyInt())).thenReturn(Optional.of(userTravelEntity));

        Optional<UserTravelModel> result = userTravelService.getUserTravel(1);

        assertTrue(result.isPresent());
        assertEquals(userTravelModel.getId(), result.get().getId());
    }

    @Test
    void testGetUserTravelsByUser() {
        when(userTravelRepository.findByUser_Id(anyInt())).thenReturn(List.of(userTravelEntity));

        List<UserTravelModel> result = userTravelService.getUserTravelsByUser(1);

        assertFalse(result.isEmpty());
        assertEquals(userTravelModel.getId(), result.get(0).getId());
    }

    @Test
    void testGetUserTravelsByTravelId() {
        when(userTravelRepository.findByTravel_Id(anyInt())).thenReturn(List.of(userTravelEntity));

        List<UserTravelModel> result = userTravelService.getUserTravelsByTravelId(1);

        assertFalse(result.isEmpty());
        assertEquals(userTravelModel.getId(), result.get(0).getId());
    }
}
