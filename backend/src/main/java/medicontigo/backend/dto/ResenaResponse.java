
package medicontigo.backend.dto;

import java.time.LocalDateTime;
import medicontigo.backend.modelo.Resena;

public record ResenaResponse(
    Long id,
    String nombrePaciente,
    int puntuacion,
    String comentario,
    String estado,
    LocalDateTime fechaCreacion
) {

    public static ResenaResponse desde(Resena resena) {
        return new ResenaResponse(
            resena.getId(),
            resena.getNombrePaciente(),
            resena.getPuntuacion(),
            resena.getComentario(),
            resena.getEstado(),
            resena.getFechaCreacion()
        );
    }
}
