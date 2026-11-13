package medicontigo.backend.dto;

// HU-18: sesión entregada al iniciar sesión (token JWT con vencimiento)
public record SesionPacienteResponse(
    String token,
    String rol,
    long expiraEn,
    PacienteResponse paciente
) {}
