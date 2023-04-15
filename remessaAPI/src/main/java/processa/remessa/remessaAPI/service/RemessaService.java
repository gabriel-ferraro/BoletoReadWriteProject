package processa.remessa.remessaAPI.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import processa.remessa.remessaAPI.model.Remessa;
import processa.remessa.remessaAPI.repository.RemessaRepository;

/**
 * Service de remessas
 * @author Gabriel Ferraro
 */
@Service
public class RemessaService {

    @Autowired
    private RemessaRepository remessaRepository;
    
    public List<Remessa> getAllRemessas() {
        return remessaRepository.findAll();
    }
    
    public Remessa getRemessaById(Long id) {
        return remessaRepository.findById(id)
                .orElseThrow(() -> new Error("Não foi possível encontrar a remessa; id: " + id));
    }
}
