package com.ramirezabril.mobility_sharing.util;

import com.ramirezabril.mobility_sharing.entity.EcoRank;
import com.ramirezabril.mobility_sharing.repository.EcoRankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EcoRanksUtils {

    private final EcoRankRepository ecoRankRepository;

    @Autowired
    public EcoRanksUtils(EcoRankRepository ecoRankRepository) {
        this.ecoRankRepository = ecoRankRepository;
    }

    public EcoRank getEcoRankById(Integer id) {
        if (id == null) return null;
        Optional<EcoRank> rank = ecoRankRepository.findById(id);
        return rank.orElse(null);
    }
}
