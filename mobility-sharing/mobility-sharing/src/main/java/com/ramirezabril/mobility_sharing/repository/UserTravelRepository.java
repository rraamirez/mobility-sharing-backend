package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.UserTravel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
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
}
