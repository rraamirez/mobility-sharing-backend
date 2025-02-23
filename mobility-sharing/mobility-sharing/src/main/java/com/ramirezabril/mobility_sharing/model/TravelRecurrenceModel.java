package com.ramirezabril.mobility_sharing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelRecurrenceModel {
    private Integer id;
    private LocalDateTime createdAt;
}
