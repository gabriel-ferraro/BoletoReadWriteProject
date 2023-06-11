package com.remittance.compensator.services;

import com.remittance.compensator.models.Remittance;
import com.remittance.compensator.repositories.RemittanceRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class RemessaService {

    private RemittanceRepository remessaRepository;

    @Autowired
    public RemessaService(RemittanceRepository remessaRepository) {
        this.remessaRepository = remessaRepository;
    }

    public List<Remittance> getAllRemessas() {
        return remessaRepository.findAll();
    }

    public Remittance getRemessaById(Integer id) {
        return remessaRepository.findById(id)
                .orElseThrow(() -> new Error("Não foi possível encontrar a remessa; id: " + id));
    }
}
