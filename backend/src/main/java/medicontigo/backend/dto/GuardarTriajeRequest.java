package medicontigo.backend.dto;

// HU-12: datos del resultado de triaje que se guarda en el historial
// (los envía el asistente de triaje de HU-11 al terminar la evaluación)
public record GuardarTriajeRequest(
    String sintomas,
    String nivelUrgencia,
    String especialidadRecomendada,
    String recomendacion
) {}