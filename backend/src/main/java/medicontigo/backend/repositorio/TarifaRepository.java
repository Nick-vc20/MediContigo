package medicontigo.backend.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medicontigo.backend.modelo.Tarifa;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {

    Optional<Tarifa> findByEspecialidadId(int especialidadId);

    List<Tarifa> findByEspecialidadCentroIdOrderByEspecialidadNombreAsc(int centroId);

    boolean existsByEspecialidadId(int especialidadId);
}
