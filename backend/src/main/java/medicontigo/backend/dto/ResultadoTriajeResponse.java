package medicontigo.backend.dto;

import java.time.LocalDateTime;
import medicontigo.backend.modelo.ResultadoTriaje;

// HU-12: resultado de triaje visto desde la cuenta del paciente
public record ResultadoTriajeResponse(
    Long id,
    String sintomas,
    String nivelUrgencia,
    String especialidadRecomendada,
    String recomendacion,
    LocalDateTime fechaTriaje
) {

    public static ResultadoTriajeResponse desde(ResultadoTriaje resultado) {
        return new ResultadoTriajeResponse(
            resultado.getId(),
            resultado.getSintomas(),
            resultado.getNivelUrgencia(),
            resultado.getEspecialidadRecomendada(),
            resultado.getRecomendacion(),
            resultado.getFechaTriaje()
        );
    }
}