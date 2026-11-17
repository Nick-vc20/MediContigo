package medicontigo.backend.dto;

import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.modelo.NivelOcupacion;

// HU-10 (10.2): confirmación que recibe el administrador tras actualizar la ocupación
public record OcupacionResponse(
        int centroId,
        String nombre,
        String nivelOcupacion,
        String etiqueta,
        String descripcion) {

    public static OcupacionResponse desde(CentroMedico centro, NivelOcupacion nivel) {
        return new OcupacionResponse(centro.getId(), centro.getNombre(), nivel.name(),
                nivel.getEtiqueta(), nivel.getDescripcion());
    }
}
