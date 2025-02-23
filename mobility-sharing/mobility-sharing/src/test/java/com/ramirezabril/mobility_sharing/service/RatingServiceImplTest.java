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
        when(ratingRepository.findAll()).thenReturn(List.of(new Rating()));

        var result = ratingService.retrieveRatings();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(ratingRepository, times(1)).findAll();
    }


    @Test
    void testRetrieveRating() {
        int id = 1;
        RatingModel ratingModel = new RatingModel();
        when(ratingRepository.findById(id)).thenReturn(Optional.of(new Rating()));

        var result = ratingService.retrieveRating(id);

        assertTrue(result.isPresent());
        assertEquals(ratingModel, result.get());
        verify(ratingRepository, times(1)).findById(id);
    }

    @Test
    void testAddRating() {
        RatingModel ratingModel = new RatingModel();
        UserModel userModel = new UserModel();
        when(ratingRepository.save(any())).thenReturn(new Rating());

        var result = ratingService.addRating(ratingModel, userModel);

        assertNotNull(result);
        verify(ratingRepository, times(1)).save(any());
    }

    @Test
    void testUpdateRating() {
        int ratingId = 1;
        RatingModel ratingModel = new RatingModel();
        ratingModel.setId(ratingId);
        UserModel userModel = new UserModel();
        var rating = new Rating();
        rating.setId(ratingId);
        when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(rating));
        when(ratingRepository.save(any())).thenReturn(new Rating());

        var result = ratingService.updateRating(ratingModel, userModel);

        assertTrue(result.isPresent());
        verify(ratingRepository, times(1)).findById(ratingId);
        verify(ratingRepository, times(1)).save(any());
    }

    @Test
    void testDeleteRating() {
        int id = 1;
        when(ratingRepository.findById(id)).thenReturn(Optional.of(new Rating()));
        when(ratingRepository.save(any())).thenReturn(new Rating());

        var result = ratingService.deleteRating(id);

        assertTrue(result.isPresent());
        verify(ratingRepository, times(1)).findById(id);
        verify(ratingRepository, times(1)).delete(any());
    }
}
