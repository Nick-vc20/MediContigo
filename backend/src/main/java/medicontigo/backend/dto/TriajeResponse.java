package medicontigo.backend.dto;

public record TriajeResponse(
        String nivelOrientacion,
        String mensaje,
        String especialidadSugerida,
        boolean requiereAtencionInmediata
) {
}
