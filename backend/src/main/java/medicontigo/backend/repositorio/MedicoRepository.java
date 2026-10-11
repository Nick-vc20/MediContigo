package medicontigo.backend.repositorio;

import medicontigo.backend.modelo.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Integer> {
    boolean existsByCmp(String cmp);
    boolean existsByCmpAndIdNot(String cmp, Integer id);
    List<Medico> findByCentroIdAndActivoTrue(int centroId);
    List<Medico> findByCentroId(int centroId);
}
