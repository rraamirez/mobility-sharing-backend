package com.ramirezabril.mobility_sharing.model;

import com.ramirezabril.mobility_sharing.util.TravelStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelModel {
    private Integer id;
    private UserModel driver;
    private String origin;
    private String destination;
    private LocalDate date;
    private LocalTime time;
    private Integer price;
    private LocalDateTime createdAt;
    private TravelRecurrenceModel travelRecurrenceModel;
    private Double latitudeOrigin;
    private Double longitudeOrigin;
    private Double latitudeDestination;
    private Double longitudeDestination;
    private TravelStatus status;
}
