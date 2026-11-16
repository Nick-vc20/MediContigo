package medicontigo.backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.TriajeRequest;
import medicontigo.backend.dto.TriajeResponse;
import medicontigo.backend.servicio.ReglasTriaje;
import medicontigo.backend.servicio.TriajeService;

class TriajeServiceTest {

    private TriajeService triajeService;

    @BeforeEach
    void configurar() {
        triajeService = new TriajeService(new ReglasTriaje());
    }

    private TriajeRequest crearSolicitud(
            String sintoma,
            String duracion,
            String intensidad,
            String adicionales,
            String alarma
    ) {
        return new TriajeRequest(
                sintoma,
                duracion,
                intensidad,
                adicionales,
                alarma
        );
    }

    @Test
    void debeAceptarRespuestasValidas() {
        TriajeRequest solicitud = crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "Cansancio",
                "NO"
        );

        assertDoesNotThrow(() ->
                triajeService.validarRespuestas(solicitud)
        );
    }

    @Test
    void debeRechazarSolicitudNula() {
        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> triajeService.validarRespuestas(null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
    }

    @Test
    void debeRechazarSintomaInvalido() {
        TriajeRequest solicitud = crearSolicitud(
                "INVALIDO",
                "UNO_TRES_DIAS",
                "LEVE",
                "",
                "NO"
        );

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> triajeService.validarRespuestas(solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
    }

    @Test
    void debeRechazarDuracionInvalida() {
        TriajeRequest solicitud = crearSolicitud(
                "FIEBRE",
                "INVALIDO",
                "MODERADA",
                "",
                "NO"
        );

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> triajeService.validarRespuestas(solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
    }

    @Test
    void debeRechazarIntensidadInvalida() {
        TriajeRequest solicitud = crearSolicitud(
                "TOS",
                "MENOS_24_HORAS",
                "INVALIDO",
                "",
                "NO"
        );

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> triajeService.validarRespuestas(solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
    }

    @Test
    void debeRechazarSenalesAlarmaInvalidas() {
        TriajeRequest solicitud = crearSolicitud(
                "FIEBRE",
                "UNO_TRES_DIAS",
                "LEVE",
                "",
                "INVALIDO"
        );

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> triajeService.validarRespuestas(solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
    }

    @Test
    void debeRechazarSintomasAdicionalesMuyLargos() {
        TriajeRequest solicitud = crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "a".repeat(501),
                "NO"
        );

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> triajeService.validarRespuestas(solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
    }

    @Test
    void debeAceptarQuinientosCaracteresAdicionales() {
        TriajeRequest solicitud = crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "a".repeat(500),
                "NO"
        );

        assertDoesNotThrow(() ->
                triajeService.validarRespuestas(solicitud)
        );
    }

    @Test
    void debeAdvertirCuandoReportaSenalesAlarma() {
        TriajeRequest solicitud = crearSolicitud(
                "DOLOR_ABDOMINAL",
                "MENOS_24_HORAS",
                "INTENSA",
                "",
                "SI"
        );

        TriajeResponse respuesta = triajeService.evaluar(solicitud);

        assertEquals("ALERTA_REPORTADA", respuesta.nivelOrientacion());
        assertTrue(respuesta.requiereAtencionInmediata());
        assertNotNull(respuesta.mensaje());
        assertNull(respuesta.especialidadSugerida());
    }

    @Test
    void debeOrientarCuandoNoEstaSeguroDeLasSenales() {
        TriajeRequest solicitud = crearSolicitud(
                "MALESTAR_GENERAL",
                "UNO_TRES_DIAS",
                "MODERADA",
                "",
                "NO_SE"
        );

        TriajeResponse respuesta = triajeService.evaluar(solicitud);

        assertEquals("ALERTA_NO_CONFIRMADA", respuesta.nivelOrientacion());
        assertFalse(respuesta.requiereAtencionInmediata());
        assertNotNull(respuesta.mensaje());
        assertNull(respuesta.especialidadSugerida());
    }

    @Test
    void noDebeAsignarClasificacionClinicaSinReglasValidadas() {
        TriajeRequest solicitud = crearSolicitud(
                "TOS",
                "CUATRO_SIETE_DIAS",
                "LEVE",
                "",
                "NO"
        );

        TriajeResponse respuesta = triajeService.evaluar(solicitud);

        assertEquals(
                "SIN_CLASIFICACION_CLINICA",
                respuesta.nivelOrientacion()
        );
        assertFalse(respuesta.requiereAtencionInmediata());
        assertNotNull(respuesta.mensaje());
        assertNull(respuesta.especialidadSugerida());
    }
}
