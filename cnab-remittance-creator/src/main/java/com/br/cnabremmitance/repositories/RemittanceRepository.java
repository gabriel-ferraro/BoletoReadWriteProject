package com.br.cnabremmitance.repositories;

import com.br.cnabremmitance.models.Remittance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RemittanceRepository extends JpaRepository<Remittance, Integer> {
    @Query("""
            SELECT r 
            FROM Remittance r 
            WHERE r.is_compensated = true
           """)
    List<Remittance> getCompensatedRemittances();
    
    @Query("""
            SELECT r 
            FROM Remittance r 
            WHERE r.is_compensated = false
           """)
    List<Remittance> getNonCompensatedRemittances();
}
