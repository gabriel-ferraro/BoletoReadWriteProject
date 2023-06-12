package com.remittance.compensator.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "remittance")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Remittance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "payer_name", nullable = false)
    private String payerName;
    @Column(nullable = false)
    private Double value;
    @Column(nullable = false)
    private String CPF;
    @Column(nullable = false)
    private String matricula;
    @Column(name = "emission_date", nullable = false)
    private LocalDate emissionDate;
    @Column(name = "is_compensated", nullable = false)
    private Boolean isCompensated;
}
