package medicontigo.backend.dto;

// HU-14: contadores del panel de moderación
public record ResumenModeracionResponse(
    long pendientes,
    long pendientesConAlertas,
    long publicadas,
    long rechazadas,
    long ocultas,
    long total
) {}
