package medicontigo.backend;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.TriajeRequest;
import medicontigo.backend.servicio.ReglasTriaje;
import medicontigo.backend.servicio.TriajeService;

class TriajeCajaBlancaTest {

    private TriajeService servicio;

    @BeforeEach
    void configurar() {
        servicio = new TriajeService(new ReglasTriaje());
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

    private void verificarError400(TriajeRequest solicitud) {
        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class,
                () -> servicio.validarRespuestas(solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
    }

    @Test
    void cb01SolicitudNula() {
        verificarError400(null);
    }

    @Test
    void cb02SintomaNulo() {
        verificarError400(crearSolicitud(
                null,
                "UNO_TRES_DIAS",
                "LEVE",
                "",
                "NO"
        ));
    }

    @Test
    void cb03SintomaInvalido() {
        verificarError400(crearSolicitud(
                "INVALIDO",
                "UNO_TRES_DIAS",
                "LEVE",
                "",
                "NO"
        ));
    }

    @Test
    void cb04DuracionNula() {
        verificarError400(crearSolicitud(
                "DOLOR_CABEZA",
                null,
                "LEVE",
                "",
                "NO"
        ));
    }

    @Test
    void cb05DuracionInvalida() {
        verificarError400(crearSolicitud(
                "DOLOR_CABEZA",
                "INVALIDO",
                "LEVE",
                "",
                "NO"
        ));
    }

    @Test
    void cb06IntensidadNula() {
        verificarError400(crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                null,
                "",
                "NO"
        ));
    }

    @Test
    void cb07IntensidadInvalida() {
        verificarError400(crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "INVALIDO",
                "",
                "NO"
        ));
    }

    @Test
    void cb08SenalesAlarmaNulas() {
        verificarError400(crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "",
                null
        ));
    }

    @Test
    void cb09SenalesAlarmaInvalidas() {
        verificarError400(crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "",
                "INVALIDO"
        ));
    }

    @Test
    void cb10DescripcionSuperiorAlLimite() {
        verificarError400(crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "a".repeat(501),
                "NO"
        ));
    }

    @Test
    void cb11DescripcionNula() {
        TriajeRequest solicitud = crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                null,
                "NO"
        );

        assertDoesNotThrow(() -> servicio.validarRespuestas(solicitud));
    }

    @Test
    void cb12DescripcionEnElLimite() {
        TriajeRequest solicitud = crearSolicitud(
                "DOLOR_CABEZA",
                "UNO_TRES_DIAS",
                "LEVE",
                "a".repeat(500),
                "NO"
        );

        assertDoesNotThrow(() -> servicio.validarRespuestas(solicitud));
    }
}
