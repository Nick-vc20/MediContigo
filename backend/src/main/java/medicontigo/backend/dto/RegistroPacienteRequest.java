package medicontigo.backend.dto;

public record RegistroPacienteRequest(
    String nombreCompleto,
    String correo,
    String contrasena,
    Boolean aceptaTratamientoDatos
) {}
