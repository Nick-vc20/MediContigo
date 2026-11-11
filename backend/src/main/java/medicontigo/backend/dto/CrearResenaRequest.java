
package medicontigo.backend.dto;

public record CrearResenaRequest(
    String nombrePaciente,
    Integer puntuacion,
    String comentario
) {}
