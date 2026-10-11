
package medicontigo.backend.repositorio;

import medicontigo.backend.modelo.CentroMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentroMedicoRepository extends JpaRepository<CentroMedico, Integer> {

    boolean existsByNombreIgnoreCaseAndDireccionIgnoreCase(String nombre, String direccion);

}
