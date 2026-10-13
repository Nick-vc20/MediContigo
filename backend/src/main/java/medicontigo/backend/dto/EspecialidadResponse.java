
package medicontigo.backend.dto;

import medicontigo.backend.modelo.Especialidad;
import java.time.LocalDateTime;

public record EspecialidadResponse(Integer id, Integer centroId, String nombre, String descripcion,
                                   boolean estado, LocalDateTime fechaActualizacion) {
    public static EspecialidadResponse desde(Especialidad e) {
        return new EspecialidadResponse(e.getId(), e.getCentro().getId(), e.getNombre(),
                e.getDescripcion(), e.isEstado(), e.getFechaActualizacion());
    }
}