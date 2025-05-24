package com.ramirezabril.mobility_sharing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eco_rank")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EcoRank {

    @Id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;
}
