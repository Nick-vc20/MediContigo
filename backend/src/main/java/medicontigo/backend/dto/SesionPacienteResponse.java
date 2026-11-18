package medicontigo.backend.dto;

public record SesionPacienteResponse(
    String token,
    String rol,
    long expiraEn,
    PacienteResponse paciente
) {}
