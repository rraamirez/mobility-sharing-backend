package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.converter.RatingConverter;
import com.ramirezabril.mobility_sharing.entity.Rating;
import com.ramirezabril.mobility_sharing.model.RatingModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.repository.RatingRepository;
import com.ramirezabril.mobility_sharing.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RatingServiceImplTest {
    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingConverter ratingConverter;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrieveRatings() {
        // Arrange
        when(ratingRepository.findAll()).thenReturn(List.of(new Rating())); // Mock response without any() here

        // Act
        var result = ratingService.retrieveRatings();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(ratingRepository, times(1)).findAll();
    }


    @Test
    void testRetrieveRating() {
        // Arrange
        int id = 1;
        RatingModel ratingModel = new RatingModel();
        when(ratingRepository.findById(id)).thenReturn(Optional.of(new Rating())); // mock repository response

        // Act
        var result = ratingService.retrieveRating(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ratingModel, result.get());
        verify(ratingRepository, times(1)).findById(id);
    }

    @Test
    void testAddRating() {
        // Arrange
        RatingModel ratingModel = new RatingModel();
        UserModel userModel = new UserModel();
        when(ratingRepository.save(any())).thenReturn(new Rating()); // mock save

        // Act
        var result = ratingService.addRating(ratingModel, userModel);

        // Assert
        assertNotNull(result);
        verify(ratingRepository, times(1)).save(any());
    }

    @Test
    void testUpdateRating() {
        // Arrange
        int ratingId = 1;
        RatingModel ratingModel = new RatingModel();
        ratingModel.setId(ratingId);
        UserModel userModel = new UserModel();
        var rating = new Rating();
        rating.setId(ratingId);
        when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(rating));
        when(ratingRepository.save(any())).thenReturn(new Rating());

        // Act
        var result = ratingService.updateRating(ratingModel, userModel);

        // Assert
        assertTrue(result.isPresent());
        verify(ratingRepository, times(1)).findById(ratingId);
        verify(ratingRepository, times(1)).save(any());
    }

    @Test
    void testDeleteRating() {
        // Arrange
        int id = 1;
        RatingModel ratingModel = new RatingModel();
        when(ratingRepository.findById(id)).thenReturn(Optional.of(new Rating()));
        when(ratingRepository.save(any())).thenReturn(new Rating());

        // Act
        var result = ratingService.deleteRating(id);

        // Assert
        assertTrue(result.isPresent());
        verify(ratingRepository, times(1)).findById(id);
        verify(ratingRepository, times(1)).delete(any());
    }
}
