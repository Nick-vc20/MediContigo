package medicontigo.backend.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medicontigo.backend.modelo.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Los correos se guardan normalizados en minúsculas
    Optional<Paciente> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
