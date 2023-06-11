package com.br.cnabremmitance.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "remittance")
public class Remittance {
    
    public Remittance(String nomePagador, Double valorTitulo, String cpf, String matricula, LocalDate emisionDate) {
        this.payerName = nomePagador;
        this.value = valorTitulo;
        this.CPF = cpf;
        this.matricula = matricula;
        this.emissionDate = emisionDate;
        this.isCompensated = false;
    }
    
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
