package com.ramirezabril.mobility_sharing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 15)
    private String nickname;

    @Column(name = "rupee_wallet", nullable = false)
    private Integer rupeeWallet = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
