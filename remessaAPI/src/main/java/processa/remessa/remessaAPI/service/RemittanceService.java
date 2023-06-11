package processa.remessa.remessaAPI.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import processa.remessa.remessaAPI.model.Remittance;
import processa.remessa.remessaAPI.repository.RemittanceRepository;

/**
 * Service de remessas
 * @author Gabriel Ferraro
 */
@Service
public class RemittanceService {

    @Autowired
    private RemittanceRepository remessaRepository;
    
    public List<Remittance> getAllRemittances() {
        return remessaRepository.findAll();
    }
    
    public Remittance getRemittanceById(Long id) {
        return remessaRepository.findById(id)
                .orElseThrow(() -> new Error("Não foi possível encontrar a remessa; id: " + id));
    }
}
