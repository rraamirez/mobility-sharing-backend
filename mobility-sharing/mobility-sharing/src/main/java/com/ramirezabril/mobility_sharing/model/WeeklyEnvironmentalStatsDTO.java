package com.ramirezabril.mobility_sharing.model;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeeklyEnvironmentalStatsDTO {
    private double averagePassengersPerCompletedTrip;
    private int confirmedRides;
    private int weeklyRupees;
    private double co2SavedKg;
    private double co2SavedKgTotal;
}
