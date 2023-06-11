package com.br.cnabremmitance.controllers;

import com.br.cnabremmitance.models.Remittance;
import com.br.cnabremmitance.services.RemittanceService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("remittances")
public class RemittanceController {
    private final RemittanceService remittanceService;
    
    public RemittanceController(RemittanceService remittanceService) {
        this.remittanceService = remittanceService;
    }
    
    @GetMapping
    public ResponseEntity<List<Remittance>> getAllRemmitances() {
        return ResponseEntity.ok().body(remittanceService.findAll());
    }
    
    @GetMapping(path = "/{id}")
    public ResponseEntity<Remittance> getRemittance(@PathVariable Integer id) {
        return ResponseEntity.ok().body(remittanceService.findById(id));
    }
    
    @GetMapping(path = "/compensated")
    public ResponseEntity<List<Remittance>> getCompensatedRemittances() {
        return ResponseEntity.ok().body(remittanceService.getCompensatedRemittances());
    }
    
    @GetMapping(path = "/non-compensated")
    public ResponseEntity<List<Remittance>> getNonCompensatedRemittances() {
        return ResponseEntity.ok().body(remittanceService.getNonCompensatedRemittances());
    }
    
    @PutMapping("/id/{id}")
    public ResponseEntity<String> compensateRemittance(@PathVariable("id") Integer remittanceId) {
        remittanceService.compensateRemittance(remittanceId);
        return ResponseEntity.ok().body("Remessa " + "de id " + remittanceId + " compensada");
    }
    
    @PutMapping(path = "/{amountToProcess}")
    public ResponseEntity<String> compensateRemittances(@PathVariable("amountToProcess") Integer amountToProcess) {
        remittanceService.compensateRemittances();
        return ResponseEntity.ok().body("Todas remessas compensadas");
    }
    
    @PostMapping
    public ResponseEntity<String> createRemittances(@PathVariable("amountToCreate") Integer amountToCreate) {
        remittanceService.createRemittances(amountToCreate);
        return ResponseEntity.ok().body(amountToCreate + " remessas criadas");
    }
}
