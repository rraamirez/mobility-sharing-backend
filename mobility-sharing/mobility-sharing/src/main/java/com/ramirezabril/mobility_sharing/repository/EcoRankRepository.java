package com.ramirezabril.mobility_sharing.repository;

import com.ramirezabril.mobility_sharing.entity.EcoRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface EcoRankRepository extends JpaRepository<EcoRank, Serializable> {
}
