package medicontigo.backend.dto;

public record ResumenModeracionResponse(
    long pendientes,
    long pendientesConAlertas,
    long publicadas,
    long rechazadas,
    long ocultas,
    long total
) {}
