package medicontigo.backend.dto;

public record LoginPacienteRequest(
    String correo,
    String contrasena
) {}
