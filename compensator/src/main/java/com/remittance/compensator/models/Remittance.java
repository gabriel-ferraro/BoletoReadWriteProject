package com.remittance.compensator.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "pagador", nullable = false)
    private String pagador;

    @Column(name = "nome_beneficiario", nullable = false)
    private String nomeBeneficiario;

    @Column(name = "vencimento_remessa", nullable = false)
    private Date vencimentoRemessa;

    @Column(name = "valor", nullable = false)
    private Double valorRemessa;
}
