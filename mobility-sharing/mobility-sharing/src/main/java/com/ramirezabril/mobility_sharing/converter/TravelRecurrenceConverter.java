package com.ramirezabril.mobility_sharing.converter;

import com.ramirezabril.mobility_sharing.entity.TravelRecurrence;
import com.ramirezabril.mobility_sharing.model.TravelRecurrenceModel;

public class TravelRecurrenceConverter {
    public static TravelRecurrenceModel entityToModel(TravelRecurrence entity) {
        if (entity == null) return null;
        TravelRecurrenceModel model = new TravelRecurrenceModel();
        model.setId(entity.getId());
        model.setCreatedAt(entity.getCreatedAt());
        return model;
    }

    public static TravelRecurrence modelToEntity(TravelRecurrenceModel model) {
        if (model == null) return null;
        TravelRecurrence entity = new TravelRecurrence();
        entity.setId(model.getId());
        entity.setCreatedAt(model.getCreatedAt());
        return entity;
    }
}
