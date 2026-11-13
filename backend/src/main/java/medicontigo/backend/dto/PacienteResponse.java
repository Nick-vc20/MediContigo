package medicontigo.backend.dto;

import java.time.LocalDateTime;
import medicontigo.backend.modelo.Paciente;

// Datos públicos del paciente: nunca incluye la contraseña ni su hash
public record PacienteResponse(
    Long id,
    String nombreCompleto,
    String correo,
    LocalDateTime fechaRegistro
) {

    public static PacienteResponse desde(Paciente paciente) {
        return new PacienteResponse(
            paciente.getId(),
            paciente.getNombreCompleto(),
            paciente.getCorreo(),
            paciente.getFechaRegistro()
        );
    }
}
