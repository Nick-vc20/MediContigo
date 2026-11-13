package medicontigo.backend.dto;

/**
 * HU-14: decisión del administrador sobre una reseña.
 * accion: APROBAR | RECHAZAR | OCULTAR
 * motivo: obligatorio para RECHAZAR y OCULTAR (queda en la auditoría)
 * confirmarAlertas: necesario para aprobar una reseña marcada por el filtro de contenido
 */
public record ModerarResenaRequest(
    String accion,
    String motivo,
    Boolean confirmarAlertas
) {}
