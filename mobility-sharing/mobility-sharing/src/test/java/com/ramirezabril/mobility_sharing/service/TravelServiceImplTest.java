package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.converter.TravelConverter;
import com.ramirezabril.mobility_sharing.model.TravelModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.repository.TravelRepository;
import com.ramirezabril.mobility_sharing.service.impl.TravelServiceImpl;
import com.ramirezabril.mobility_sharing.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TravelServiceImplTest {

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private UserService userService;

    @Mock
    private TokenUtil tokenUtil;

    @InjectMocks
    private TravelServiceImpl travelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTravelById_Success() {
        // Arrange
        Integer travelId = 1;
        TravelModel travelModel = new TravelModel();
        when(travelRepository.findById(travelId)).thenReturn(Optional.of(TravelConverter.toTravelEntity(travelModel)));

        // Act
        Optional<TravelModel> result = travelService.getTravelById(travelId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(travelModel, result.get());
    }

    @Test
    void testGetTravelById_NotFound() {
        // Arrange
        Integer travelId = 1;
        when(travelRepository.findById(travelId)).thenReturn(Optional.empty());

        // Act
        Optional<TravelModel> result = travelService.getTravelById(travelId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateTravel() {
        // Arrange
        TravelModel travelModel = new TravelModel();
        UserModel userModel = new UserModel();
        travelModel.setDriver(userModel);
        when(travelRepository.save(any())).thenReturn(TravelConverter.toTravelEntity(travelModel));

        // Act
        TravelModel result = travelService.createTravel(travelModel, userModel);

        // Assert
        assertNotNull(result);
        verify(travelRepository, times(1)).save(any());
    }

    @Test
    void testUpdateTravel_Success() {
        // Arrange
        TravelModel travelModel = new TravelModel();
        travelModel.setId(1);
        UserModel userModel = new UserModel();
        TravelModel existingTravelModel = new TravelModel();
        when(travelRepository.findById(travelModel.getId())).thenReturn(Optional.of(TravelConverter.toTravelEntity(existingTravelModel)));
        when(travelRepository.save(any())).thenReturn(TravelConverter.toTravelEntity(travelModel));

        // Act
        Optional<TravelModel> result = travelService.updateTravel(travelModel, userModel);

        // Assert
        assertTrue(result.isPresent());
        verify(travelRepository, times(1)).findById(any());
        verify(travelRepository, times(1)).save(any());
    }

    @Test
    void testUpdateTravel_NotFound() {
        // Arrange
        TravelModel travelModel = new TravelModel();
        travelModel.setId(1);
        UserModel userModel = new UserModel();
        when(travelRepository.findById(travelModel.getId())).thenReturn(Optional.empty());

        // Act
        Optional<TravelModel> result = travelService.updateTravel(travelModel, userModel);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteTravel() {
        // Arrange
        Integer travelId = 1;
        doNothing().when(travelRepository).deleteById(travelId);

        // Act
        travelService.deleteTravel(travelId);

        // Assert
        verify(travelRepository, times(1)).deleteById(travelId);
    }

    @Test
    void testGetTravelsByDriver() {
        // Arrange
        Integer driverId = 1;
        TravelModel travelModel = new TravelModel();
        when(travelRepository.findByDriverId(driverId)).thenReturn(List.of(TravelConverter.toTravelEntity(travelModel)));

        // Act
        List<TravelModel> result = travelService.getTravelsByDriver(driverId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(travelRepository, times(1)).findByDriverId(driverId);
    }

    @Test
    void testGetTravelsByOriginAndDestination() {
        // Arrange
        String origin = "City A";
        String destination = "City B";
        TravelModel travelModel = new TravelModel();
        when(travelRepository.findByOriginAndDestination(origin, destination)).thenReturn(List.of(TravelConverter.toTravelEntity(travelModel)));

        // Act
        List<TravelModel> result = travelService.getTravelsByOriginAndDestination(origin, destination);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(travelRepository, times(1)).findByOriginAndDestination(origin, destination);
    }
}
