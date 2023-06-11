package com.br.cnabremmitance.services;

import com.br.cnabremmitance.models.Remittance;
import com.br.cnabremmitance.repositories.RemittanceRepository;
import com.br.cnabremmitance.utils.CnabWriter;
import com.br.cnabremmitance.utils.MessageSender;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RemittanceService {

    private final RemittanceRepository RemittanceRepository;
    private final MessageSender msgSender;
    private final CnabWriter cnabWriter;

    @Autowired
    public RemittanceService(RemittanceRepository remittanceRepository, MessageSender msgSender, CnabWriter cnabWriter) {
        this.RemittanceRepository = remittanceRepository;
        this.cnabWriter = cnabWriter;
        this.msgSender = msgSender;
    }

    public List<Remittance> findAll() {
        return RemittanceRepository.findAll();
    }

    public Remittance findById(Integer id) {
        return RemittanceRepository.findById(id)
                .orElseThrow(()
                        -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Remittance not found"));
    }

    public List<Remittance> getCompensatedRemittances() {
        return RemittanceRepository.getCompensatedRemittances();
    }

    public List<Remittance> getNonCompensatedRemittances() {
        return RemittanceRepository.getNonCompensatedRemittances();
    }

    public void createRemittances(Integer amount) {
        try {
            cnabWriter.generateRemittances(amount, null, null);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(RemittanceService.class.getName()).log(Level.SEVERE, "Excecao ocorreu ao processar remessas", ex);
        }
    }

    public void requestToCompensateRemittances(Integer amount) {
        try {
            this.msgSender.requestToCompensateRemittances(amount, null);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(RemittanceService.class.getName()).log(Level.SEVERE, "Excecao ocorreu ao enviar requisicao para aplicacao de compensacao", ex);
        }
    }

    public void createAndCompensateRemittances(Integer amount) {
        createRemittances(amount);
        requestToCompensateRemittances(amount);
    }
}
