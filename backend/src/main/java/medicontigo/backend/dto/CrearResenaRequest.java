package medicontigo.backend.dto;

// HU-13 / HU-18: el nombre del autor se toma del paciente autenticado
public record CrearResenaRequest(
    Integer puntuacion,
    String comentario
) {}
