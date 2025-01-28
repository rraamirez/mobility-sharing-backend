package com.ramirezabril.mobility_sharing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rating")
@Data
@Getter
@Setter
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "rating_user_id", nullable = false)
    private User ratingUser;

    @ManyToOne
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser;

    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;

    @Column(nullable = false)
    private Integer rating;

    @Column
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
