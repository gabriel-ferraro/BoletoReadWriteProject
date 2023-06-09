package com.br.cnabremmitance.services;

import com.br.cnabremmitance.models.Remittance;
import com.br.cnabremmitance.repositories.RemittanceRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RemittanceService {
    private final RemittanceRepository RemittanceRepository;

    public RemittanceService(RemittanceRepository RemittanceRepository) {
        this.RemittanceRepository = RemittanceRepository;
    }
    
    public List<Remittance> findAll() {
        return RemittanceRepository.findAll();
    }
    
    public Remittance findById(Integer id) {
        return RemittanceRepository.findById(id)
                .orElseThrow(() -> 
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, 
                                "Remittance not found"));
    }
    
    public List<Remittance> getCompensatedRemittances() {
        return RemittanceRepository.getCompensatedRemittances();
    }
    
    public List<Remittance> getNonCompensatedRemittances() {
        return RemittanceRepository.getNonCompensatedRemittances();
    }
    
    public void compensateRemittance(Integer id) {
        RemittanceRepository.compensateRemittance(id);
    }
    
    public void compensateRemittances() {
        RemittanceRepository.compensateRemittances();
    }
    
    public void createRemittances(Integer amount) {
        
    }
}
