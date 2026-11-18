package medicontigo.backend.dto;


public record ModerarResenaRequest(
    String accion,
    String motivo,
    Boolean confirmarAlertas
) {}
