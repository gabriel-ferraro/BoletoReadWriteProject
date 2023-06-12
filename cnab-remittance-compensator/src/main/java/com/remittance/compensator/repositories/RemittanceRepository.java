package com.remittance.compensator.repositories;

import com.remittance.compensator.models.Remittance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemittanceRepository extends JpaRepository<Remittance, Integer> {}
