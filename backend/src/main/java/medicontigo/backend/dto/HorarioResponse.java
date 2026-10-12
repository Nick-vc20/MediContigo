
package medicontigo.backend.dto;

import medicontigo.backend.modelo.HorarioEspecialidad;
import java.time.LocalTime;

public record HorarioResponse(Integer id, Integer especialidadId, int diaSemana,
                              LocalTime horaInicio, LocalTime horaFin) {
    public static HorarioResponse desde(HorarioEspecialidad h) {
        return new HorarioResponse(h.getId(), h.getEspecialidad().getId(), h.getDiaSemana(),
                h.getHoraInicio(), h.getHoraFin());
    }
}