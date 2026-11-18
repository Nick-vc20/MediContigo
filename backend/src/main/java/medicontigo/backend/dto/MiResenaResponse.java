package medicontigo.backend.dto;

import java.time.LocalDateTime;

import medicontigo.backend.modelo.Resena;


public record MiResenaResponse(
    Long id,
    Integer centroId,
    String centroNombre,
    int puntuacion,
    String comentario,
    String estado,
    LocalDateTime fechaCreacion,
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
