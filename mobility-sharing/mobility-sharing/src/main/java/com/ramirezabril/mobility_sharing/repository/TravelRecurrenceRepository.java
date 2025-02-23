package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.TravelRecurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface TravelRecurrenceRepository extends JpaRepository<TravelRecurrence, Serializable> {
}
