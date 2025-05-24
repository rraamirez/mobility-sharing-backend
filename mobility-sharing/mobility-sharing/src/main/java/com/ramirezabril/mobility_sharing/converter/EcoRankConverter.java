package com.ramirezabril.mobility_sharing.converter;

import com.ramirezabril.mobility_sharing.entity.EcoRank;
import com.ramirezabril.mobility_sharing.model.EcoRankModel;

public class EcoRankConverter {

    public static EcoRank toEntity(EcoRankModel model) {
        if (model == null) return null;
        return EcoRank.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }

    public static EcoRankModel toModel(EcoRank entity) {
        if (entity == null) return null;
        return EcoRankModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
