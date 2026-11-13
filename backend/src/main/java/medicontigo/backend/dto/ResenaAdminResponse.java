package medicontigo.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import medicontigo.backend.modelo.Resena;

// HU-14: reseña vista desde el panel de auditoría (incluye alertas y datos de moderación)
public record ResenaAdminResponse(
    Long id,
    Integer centroId,
    String centroNombre,
    String nombrePaciente,
    int puntuacion,
    String comentario,
    String estado,
    List<String> alertas,
    LocalDateTime fechaCreacion,
    String motivoModeracion,
    String moderadoPor,
    LocalDateTime fechaModeracion
) {

    public static ResenaAdminResponse desde(Resena resena) {
        return new ResenaAdminResponse(
            resena.getId(),
            resena.getCentro().getId(),
            resena.getCentro().getNombre(),
            resena.getNombrePaciente(),
            resena.getPuntuacion(),
            resena.getComentario(),
            resena.getEstado(),
            resena.getAlertas(),
            resena.getFechaCreacion(),
            resena.getMotivoModeracion(),
            resena.getModeradoPor(),
            resena.getFechaModeracion()
        );
    }
}
