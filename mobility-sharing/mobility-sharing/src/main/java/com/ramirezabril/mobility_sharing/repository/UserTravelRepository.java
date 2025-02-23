package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.UserTravel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface UserTravelRepository extends JpaRepository<UserTravel, Serializable> {
    List<UserTravel> findByUser_Id(int id);

    List<UserTravel> findByTravel_Id(int id);

}
