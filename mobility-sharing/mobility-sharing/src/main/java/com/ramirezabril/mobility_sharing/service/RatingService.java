package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.model.RatingModel;
import com.ramirezabril.mobility_sharing.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface RatingService {
    List<RatingModel> retrieveRatings();

    Optional<RatingModel> retrieveRating(int id);

    RatingModel addRating(RatingModel rating, UserModel raterUsername);

    Optional<RatingModel> updateRating(RatingModel rating, UserModel raterUsername);

    Optional<RatingModel> deleteRating(int id);

    List<RatingModel> retrieveRatingsByRatingUser(int userId);

    List<RatingModel> retrieveRatingsByRatedUser(int userId);

    List<RatingModel> retrieveRatingsByTravel(int userId);
}
