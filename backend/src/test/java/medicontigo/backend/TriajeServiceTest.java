
package medicontigo.backend;

import medicontigo.backend.dto.TriajeRequest;
import medicontigo.backend.servicio.TriajeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class TriajeServiceTest {

    private TriajeService servicio;

    @BeforeEach
    void preparar() {
        servicio = new TriajeService();
    }

    private TriajeRequest solicitudValida() {
        return new TriajeRequest(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "Cansancio",
                "NO"
        );
    }

    @Test
    void debeAceptarCuestionarioValido() {
        assertDoesNotThrow(
                () -> servicio.validarRespuestas(solicitudValida())
        );
    }

    @Test
    void debeRechazarCuestionarioNulo() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.validarRespuestas(null)
        );

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void debeRechazarSintomaInvalido() {
        TriajeRequest solicitud = new TriajeRequest(
                "SINTOMA_DESCONOCIDO",
                "UNO_TRES_DIAS",
                "LEVE",
                null,
                "NO"
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.validarRespuestas(solicitud)
        );

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void debeRechazarDuracionInvalida() {
        TriajeRequest solicitud = new TriajeRequest(
                "DOLOR_CABEZA",
                "DURACION_DESCONOCIDA",
                "LEVE",
                null,
                "NO"
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.validarRespuestas(solicitud)
        );

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void debeRechazarIntensidadInvalida() {
        TriajeRequest solicitud = new TriajeRequest(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "INTENSIDAD_DESCONOCIDA",
                null,
                "NO"
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.validarRespuestas(solicitud)
        );

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void debeRechazarSenalesAlarmaInvalidas() {
        TriajeRequest solicitud = new TriajeRequest(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                null,
                "RESPUESTA_INVALIDA"
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.validarRespuestas(solicitud)
        );

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void debeRechazarDescripcionMuyLarga() {
        TriajeRequest solicitud = new TriajeRequest(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "A".repeat(501),
                "NO"
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> servicio.validarRespuestas(solicitud)
        );

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void debeAceptarDescripcionDe500Caracteres() {
        TriajeRequest solicitud = new TriajeRequest(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "A".repeat(500),
                "NO"
        );

        assertDoesNotThrow(
                () -> servicio.validarRespuestas(solicitud)
        );
    }
}
