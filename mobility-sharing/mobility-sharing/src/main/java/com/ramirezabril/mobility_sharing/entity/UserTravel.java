package com.ramirezabril.mobility_sharing.entity;

import com.ramirezabril.mobility_sharing.util.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_travel")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTravel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
