package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Serializable> {
    @Query(value = "SELECT * FROM travel t WHERE driver_id = ?1", nativeQuery = true)
    List<Travel> findByDriverId(int driverId);

    @Query(value = "SELECT * FROM travel t WHERE origin like %?1% AND destination like %?2% AND driver_id != ?3 AND t.date >= CURDATE() ", nativeQuery = true)
    List<Travel> findByOriginAndDestination(String origin, String destination, Integer userId);

    @Query(value = "SELECT * FROM travel t WHERE t.travel_recurrence_id IS NOT NULL ORDER BY t.travel_recurrence_id", nativeQuery = true)
    List<Travel> getRecurringTravels();

    @Query(value = """
                SELECT t.* FROM travel t
                JOIN user_travel ut ON t.id = ut.travel_id
                WHERE ut.user_id = ?1
                AND NOT EXISTS (
                    SELECT 1 FROM rating r WHERE r.travel_id = t.id AND r.rating_user_id = ?1
                )
            """, nativeQuery = true)
    List<Travel> findUnratedTravelsByUserId(int userId);
}
