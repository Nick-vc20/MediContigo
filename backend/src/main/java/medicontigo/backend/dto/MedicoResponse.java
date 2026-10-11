package medicontigo.backend.dto;

import medicontigo.backend.modelo.Medico;
import java.time.LocalDate;

// Respuesta plana para evitar devolver relaciones JPA completas.
public record MedicoResponse(Integer id, String nombreCompleto, String cmp,
                             Integer especialidadId, String especialidad,
                             Integer centroId, boolean activo, LocalDate fechaIngreso) {
    public static MedicoResponse desde(Medico medico) {
        return new MedicoResponse(medico.getId(), medico.getNombreCompleto(), medico.getCmp(),
                medico.getEspecialidad().getId(), medico.getEspecialidad().getNombre(),
                medico.getCentro().getId(), medico.isActivo(), medico.getFechaIngreso());
    }
}
