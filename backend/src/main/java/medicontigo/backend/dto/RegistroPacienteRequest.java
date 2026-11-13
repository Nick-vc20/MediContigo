package medicontigo.backend.dto;

// HU-17: datos del formulario de registro de paciente
public record RegistroPacienteRequest(
    String nombreCompleto,
    String correo,
    String contrasena,
    Boolean aceptaTratamientoDatos
) {}
