package com.ramirezabril.mobility_sharing.converter;

import com.ramirezabril.mobility_sharing.entity.Rating;
import com.ramirezabril.mobility_sharing.model.RatingModel;

public class RatingConverter {

    public static RatingModel toRatingModel(Rating rating) {
        return rating == null ? null : new RatingModel(
                rating.getId(),
                UserConverter.toUserModel(rating.getRatingUser()),
                UserConverter.toUserModel(rating.getRatedUser()),
                TravelConverter.toTravelModel(rating.getTravel()),
                rating.getRating(),
                rating.getComment(),
                rating.getCreatedAt()
        );
    }

    public static Rating toRatingEntity(RatingModel ratingModel) {
        if (ratingModel == null) {
            return null;
        }

        Rating rating = new Rating();
        rating.setId(ratingModel.getId());
        rating.setRatingUser(UserConverter.toUserEntity(ratingModel.getRatingUser()));
        rating.setRatedUser(UserConverter.toUserEntity(ratingModel.getRatedUser()));
        rating.setTravel(TravelConverter.toTravelEntity(ratingModel.getTravel()));
        rating.setRating(ratingModel.getRating());
        rating.setComment(ratingModel.getComment());
        rating.setCreatedAt(ratingModel.getCreatedAt());

        return rating;
    }
}
