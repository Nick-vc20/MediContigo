package medicontigo.backend.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import medicontigo.backend.modelo.HorarioEspecialidad;

public interface HorarioEspecialidadRepository extends JpaRepository<HorarioEspecialidad, Integer> {
    List<HorarioEspecialidad> findByEspecialidadIdOrderByDiaSemanaAscHoraInicioAsc(int especialidadId);
    List<HorarioEspecialidad> findByEspecialidadIdAndDiaSemana(int especialidadId, int diaSemana);
    List<HorarioEspecialidad> findByEspecialidadCentroIdOrderByDiaSemanaAscHoraInicioAsc(int centroId);
}
