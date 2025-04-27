package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Serializable> {
    @Query(value = "SELECT * FROM travel t WHERE driver_id = ?1 ORDER BY t.date DESC", nativeQuery = true)
    List<Travel> findByDriverId(int driverId);


    //distinct could be avoided addings constraint like this: ALTER TABLE user_travel ADD CONSTRAINT unique_user_travel UNIQUE (user_id, travel_id);

    @Query(value = "SELECT DISTINCT t.* FROM travel t JOIN user_travel ut ON t.id = ut.travel_id WHERE ut.user_id = ?1 AND t.driver_id != ?1 AND ut.status != 'canceled'", nativeQuery = true)
    List<Travel> findEnrolledTravelsByUserId(int userId);

//    @Query(value = "SELECT * FROM travel t WHERE origin like %?1% AND destination like %?2% AND driver_id != ?3 AND t.date >= CURDATE() ", nativeQuery = true)
//    List<Travel> findByOriginAndDestination(String origin, String destination, Integer userId);


    //This select is much more permissive than the one in the behind, ensuring better
    // experience while looking for travels (uppercase, uncompleted destinations etc) Destination is not mandatory
    @Query(value = "SELECT DISTINCT t.* FROM travel t " +
            "WHERE LOWER(t.origin) LIKE LOWER(CONCAT('%', :origin, '%')) " +
            "AND (:destination IS NULL OR LOWER(t.destination) LIKE LOWER(CONCAT('%', :destination, '%'))) " +
            "AND t.driver_id != :userId " +
            "AND t.date >= CURDATE() " +
            "AND t.status = 'ACTIVE' " +
            "AND NOT EXISTS (SELECT 1 FROM user_travel ut2 WHERE ut2.travel_id = t.id AND ut2.user_id = :userId)",
            nativeQuery = true)
    List<Travel> findByOriginAndDestination(@Param("origin") String origin,
                                            @Param("destination") String destination,
                                            @Param("userId") Integer userId);


    @Query(value = "SELECT * FROM travel t WHERE t.travel_recurrence_id IS NOT NULL ORDER BY t.travel_recurrence_id", nativeQuery = true)
    List<Travel> getRecurringTravels();

    @Query(value = """
                SELECT DISTINCT t.* FROM travel t
                JOIN user_travel ut ON t.id = ut.travel_id
                WHERE ut.user_id = ?1
                AND ut.status = 'confirmed'
                AND t.status != 'CANCELED'
                AND NOT EXISTS (
                    SELECT 1 FROM rating r WHERE r.travel_id = t.id AND r.rating_user_id = ?1
                )
            """, nativeQuery = true)
    List<Travel> findUnratedTravelsByUserId(int userId);
}
