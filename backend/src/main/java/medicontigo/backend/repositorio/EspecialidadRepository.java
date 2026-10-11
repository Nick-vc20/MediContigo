package medicontigo.backend.repositorio;

import medicontigo.backend.modelo.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio mínimo para consultar el catálogo mientras se desarrolla HU-03.
public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {}
