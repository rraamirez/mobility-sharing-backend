package com.ramirezabril.mobility_sharing.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingModel {

    private Integer id;
    private UserModel ratingUser;
    private UserModel ratedUser;
    private TravelModel travel;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

}
