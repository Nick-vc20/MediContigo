package medicontigo.backend.dto;

// HU-18: credenciales de inicio de sesión del paciente
public record LoginPacienteRequest(
    String correo,
    String contrasena
) {}
