package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.converter.RatingConverter;
import com.ramirezabril.mobility_sharing.model.RatingModel;
import com.ramirezabril.mobility_sharing.repository.RatingRepository;
import com.ramirezabril.mobility_sharing.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("ratingService")
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public List<RatingModel> retrieveRatings() {
        return ratingRepository.findAll().stream()
                .map(RatingConverter::toRatingModel)
                .toList();
    }

    @Override
    public Optional<RatingModel> retrieveRating(int id) {
        return ratingRepository.findById(id)
                .map(RatingConverter::toRatingModel);
    }

    @Override
    public RatingModel addRating(RatingModel rating) {
        var savedRating = ratingRepository.save(RatingConverter.toRatingEntity(rating));
        return RatingConverter.toRatingModel(savedRating);
    }

    @Override
    public Optional<RatingModel> updateRating(RatingModel rating) {
        return ratingRepository.findById(rating.getId())
                .map(existingRating -> {
                    var updatedEntity = RatingConverter.toRatingEntity(rating);
                    updatedEntity.setId(existingRating.getId());
                    var savedRating = ratingRepository.save(updatedEntity);
                    return RatingConverter.toRatingModel(savedRating);
                });
    }

    @Override
    public Optional<RatingModel> deleteRating(int id) {
        return ratingRepository.findById(id)
                .map(rating -> {
                    ratingRepository.delete(rating);
                    return RatingConverter.toRatingModel(rating);
                });
    }

    @Override
    public List<RatingModel> retrieveRatingsByRatingUser(int userId) {
        return ratingRepository.findByRatingUser(userId).stream()
                .map(RatingConverter::toRatingModel)
                .toList();
    }

    @Override
    public List<RatingModel> retrieveRatingsByRatedUser(int userId) {
        return ratingRepository.findByRatedUser(userId).stream()
                .map(RatingConverter::toRatingModel)
                .toList();
    }

    @Override
    public List<RatingModel> retrieveRatingsByTravel(int travelId) {
        return ratingRepository.findByTravel(travelId).stream()
                .map(RatingConverter::toRatingModel)
                .toList();
    }
}

