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

    @Query(value = "SELECT * FROM travel t WHERE origin like %?1% AND destination like %?2%", nativeQuery = true)
    List<Travel> findByOriginAndDestination(String origin, String destination);

    @Query(value = "SELECT * FROM travel t WHERE t.travel_recurrence_id IS NOT NULL ORDER BY t.travel_recurrence_id", nativeQuery = true)
    List<Travel> getRecurringTravels();
}
