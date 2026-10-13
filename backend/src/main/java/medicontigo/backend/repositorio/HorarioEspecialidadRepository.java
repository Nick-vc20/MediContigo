package medicontigo.backend.repositorio;

import medicontigo.backend.modelo.HorarioEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioEspecialidadRepository extends JpaRepository<HorarioEspecialidad, Integer> {
    List<HorarioEspecialidad> findByEspecialidadIdOrderByDiaSemanaAscHoraInicioAsc(int especialidadId);
    List<HorarioEspecialidad> findByEspecialidadIdAndDiaSemana(int especialidadId, int diaSemana);
}