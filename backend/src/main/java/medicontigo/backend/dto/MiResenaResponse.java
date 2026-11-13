package medicontigo.backend.dto;

import java.time.LocalDateTime;
import medicontigo.backend.modelo.Resena;

// HU-18: reseña vista desde la cuenta del paciente (incluye el centro y su estado de moderación)
public record MiResenaResponse(
    Long id,
    Integer centroId,
    String centroNombre,
    int puntuacion,
    String comentario,
    String estado,
    LocalDateTime fechaCreacion,
    // HU-14: motivo indicado por el administrador al rechazar u ocultar
    String motivoModeracion
) {

    public static MiResenaResponse desde(Resena resena) {
        return new MiResenaResponse(
            resena.getId(),
            resena.getCentro().getId(),
            resena.getCentro().getNombre(),
            resena.getPuntuacion(),
            resena.getComentario(),
            resena.getEstado(),
            resena.getFechaCreacion(),
            resena.getMotivoModeracion()
        );
    }
}
