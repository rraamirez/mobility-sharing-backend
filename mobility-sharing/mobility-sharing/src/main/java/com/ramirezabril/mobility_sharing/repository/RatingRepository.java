package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Serializable> {

    @Query(value = "SELECT * FROM rating WHERE rating_user_id = ?1", nativeQuery = true)
    List<Rating> findByRatingUser(int userId);

    @Query(value = "SELECT * FROM rating WHERE rated_user_id = ?1", nativeQuery = true)
    List<Rating> findByRatedUser(int userId);

    @Query(value = "SELECT * FROM rating WHERE travel_id = ?1", nativeQuery = true)
    List<Rating> findByTravel(int travelId);

}
