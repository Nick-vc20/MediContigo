package medicontigo.backend.servicio;

import org.springframework.stereotype.Component;

import medicontigo.backend.dto.TriajeRequest;
import medicontigo.backend.dto.TriajeResponse;

@Component
public class ReglasTriaje {

    public TriajeResponse evaluar(TriajeRequest solicitud) {

        String senalesAlarma = solicitud.senalesAlarma();

        if ("SI".equals(senalesAlarma)) {
            return new TriajeResponse(
                    "ALERTA_REPORTADA",
                    "Has indicado que presentas una señal de alarma. "
                            + "Busca atención médica de emergencia. "
                            + "No esperes una evaluación de este cuestionario.",
                    null,
                    true
            );
        }

        if ("NO_SE".equals(senalesAlarma)) {
            return new TriajeResponse(
                    "ALERTA_NO_CONFIRMADA",
                    "No estás seguro de presentar una señal de alarma. "
                            + "Solicita orientación de un profesional de salud. "
                            + "Si tienes síntomas graves, busca atención "
                            + "de emergencia inmediatamente.",
                    null,
                    false
            );
        }

        return new TriajeResponse(
                "SIN_CLASIFICACION_CLINICA",
                "Tus respuestas fueron recibidas correctamente. "
                        + "Este cuestionario todavía no cuenta con reglas "
                        + "clínicas validadas para recomendar una especialidad "
                        + "o determinar la urgencia. "
                        + "Consulta con un profesional de salud.",
                null,
                false
        );
    }
}
