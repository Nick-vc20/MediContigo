package medicontigo.backend.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medicontigo.backend.modelo.ResultadoTriaje;

// HU-12: consultas del historial de triaje, siempre filtradas por paciente
@Repository
public interface ResultadoTriajeRepository extends JpaRepository<ResultadoTriaje, Long> {

    // Historial del paciente, del más reciente al más antiguo
    List<ResultadoTriaje> findByPaciente_IdOrderByFechaTriajeDescIdDesc(Long pacienteId);

    // Un resultado solo se devuelve si pertenece al paciente
    Optional<ResultadoTriaje> findByIdAndPaciente_Id(Long id, Long pacienteId);

    long countByPaciente_Id(Long pacienteId);
}