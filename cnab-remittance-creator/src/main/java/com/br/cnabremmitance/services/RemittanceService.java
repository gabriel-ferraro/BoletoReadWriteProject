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
                .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Remittance not found")
                );
    }

    public List<Remittance> getCompensatedRemittances() {
        return RemittanceRepository.getCompensatedRemittances();
    }

    public List<Remittance> getNonCompensatedRemittances() {
        return RemittanceRepository.getNonCompensatedRemittances();
    }

    /**
     * Cria remessas no hotFolder, no BD e se flag de controle verdaderia, envia
     * requisicao para a aplicacao de compensacao ser notificada com a criacao
     * da remessa.
     *
     * @param amount Qtt de remessas a serem criadas.
     * @param requestProcessRemittances Flag para identificar se a aplicacao de
     * compensacao deve ser notificada com a criacao da remessa.
     */
    public void createRemittances(Integer amount, Boolean requestProcessRemittances) {
        try {
            cnabWriter.generateRemittances(amount, null, null, requestProcessRemittances);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(RemittanceService.class.getName()).log(
                    Level.SEVERE,
                    "Excecao ocorreu ao processar remessas. requestToProcessRemittances: " + requestProcessRemittances,
                    ex
            );
        }
    }

    /**
     * Metodo para pedir a ccmpensacao de remessa que ainda nao foi
     * compensada.
     *
     * @param remittanceId Qtt de remessas.
     */
    public void requestToCompensateRemittances(Integer remittanceId) {
        try {
            msgSender.requestToCompensateRemittance(remittanceId, null);
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(RemittanceService.class.getName()).log(
                    Level.SEVERE,
                    "Excecao ocorreu ao enviar requisicao para aplicacao de compensacao",
                    ex
            );
        }
    }
}
