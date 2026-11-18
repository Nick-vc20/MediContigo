package medicontigo.backend.dto;

public record GuardarTriajeRequest(
    String sintomas,
    String nivelUrgencia,
    String especialidadRecomendada,
    String recomendacion
) {}