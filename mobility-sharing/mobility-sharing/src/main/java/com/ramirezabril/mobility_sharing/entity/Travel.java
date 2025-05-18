package com.ramirezabril.mobility_sharing.entity;

import com.ramirezabril.mobility_sharing.util.TravelStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "travel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Travel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(nullable = false, length = 150)
    private String origin;

    @Column(nullable = false, length = 150)
    private String destination;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private Integer price = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "travel_recurrence_id")
    private TravelRecurrence travelRecurrence;

    @Column(name = "latitude_origin", nullable = true)
    private Double latitudeOrigin;

    @Column(name = "longitude_origin", nullable = true)
    private Double longitudeOrigin;

    @Column(name = "latitude_destination", nullable = true)
    private Double latitudeDestination;

    @Column(name = "longitude_destination", nullable = true)
    private Double longitudeDestination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TravelStatus status = TravelStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
