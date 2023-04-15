package processa.remessa.remessaAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import processa.remessa.remessaAPI.model.Remessa;

/**
 * Repository de remessa
 * @author Gabriel Ferraro
 */
@Repository
public interface RemessaRepository extends JpaRepository<Remessa, Long> {}
