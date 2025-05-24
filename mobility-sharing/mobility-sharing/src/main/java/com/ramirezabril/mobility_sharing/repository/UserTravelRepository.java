package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.UserTravel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTravelRepository extends JpaRepository<UserTravel, Serializable> {
    List<UserTravel> findByUser_Id(int id);

    List<UserTravel> findByTravel_Id(int id);

    @Query(value = """
                SELECT * FROM user_travel where travel_id = ?2 AND user_id = ?1 
            """, nativeQuery = true)
    List<UserTravel> findUserAndTravel(int userId, int travelId);

    @Query(value = """
                SELECT * FROM user_travel where travel_id = ?2 AND user_id = ?1 
            """, nativeQuery = true)
    Optional<UserTravel> findConcreteUserTravel(int userId, int travelId);

    @Transactional
    @Query(value = "DELETE FROM user_travel WHERE travel_id = ?1", nativeQuery = true)
    void deleteByTravelId(int travelId);

    @Query(value = "SELECT COUNT(*) FROM user_travel WHERE user_id = ?1 AND status = 'confirmed'", nativeQuery = true)
    Optional<Long> countConfirmedUserTravelsByUserId(int userId);

    @Query(value = "SELECT COUNT(*) FROM user_travel WHERE travel_id = ?1 and user_travel.status = 'confirmed'", nativeQuery = true)
    Optional<Long> countByTravelIdAndCompleted(int travelId);


    // Returns the number of confirmed travels for a user within the specified date range
    @Query(value = """
            SELECT COUNT(*) 
            FROM user_travel ut 
            JOIN travel t ON ut.travel_id = t.id 
            WHERE ut.user_id = :userId 
                AND ut.status = 'confirmed' 
                AND t.date BETWEEN :from AND :to
            """, nativeQuery = true)
    Optional<Long> countConfirmedUserTravelsByUserIdAndDateBetween(
            @Param("userId") Integer userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // Counts confirmed passenger bookings for driver’s trips (excluding driver) in the date range
    @Query(value = """
            SELECT COUNT(*) 
            FROM user_travel ut
            JOIN travel t ON ut.travel_id = t.id
            WHERE t.driver_id = :userId
                AND ut.user_id != :userId
                AND ut.status = 'confirmed' 
                AND t.date BETWEEN :from AND :to
            """, nativeQuery = true)
    Optional<Long> countUserTravelsForDriverByDateBetween(
            @Param("userId") Integer userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query(value = """
            SELECT COUNT(*) 
            FROM user_travel ut
            JOIN travel t ON ut.travel_id = t.id
            WHERE t.driver_id = :userId
              AND ut.user_id <> :userId
              AND ut.status = 'confirmed'
            """, nativeQuery = true)
    Optional<Long> countAllConfirmedPassengersForDriver(Integer userId);

}
