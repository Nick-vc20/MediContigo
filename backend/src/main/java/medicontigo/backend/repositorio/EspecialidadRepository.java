package medicontigo.backend.repositorio;

import medicontigo.backend.modelo.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {
    List<Especialidad> findByCentroIdOrderByNombreAsc(int centroId);
    List<Especialidad> findByCentroIdAndEstadoTrueOrderByNombreAsc(int centroId);
    boolean existsByCentroIdAndNombreIgnoreCase(int centroId, String nombre);
    boolean existsByCentroIdAndNombreIgnoreCaseAndIdNot(int centroId, String nombre, int id);
}