package com.ramirezabril.mobility_sharing.model;

import com.ramirezabril.mobility_sharing.util.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTravelModel {
    private Integer id;
    private UserModel user;
    private TravelModel travel;
    private Status status;
    private LocalDateTime createdAt;
}
