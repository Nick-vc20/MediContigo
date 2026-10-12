package medicontigo.backend;

import medicontigo.backend.dto.*;
import medicontigo.backend.servicio.EspecialidadService;
import medicontigo.backend.servicio.MedicoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EspecialidadServiceTest {

    private static final int CENTRO = 1; // el centro creado en data.sql

    @Autowired EspecialidadService servicio;
    @Autowired MedicoService medicoServicio;

    private EspecialidadResponse nueva(String nombre) {
        return servicio.crear(CENTRO, new EspecialidadRequest(nombre, null));
    }

    private int codigoHttp(Executable accion) {
        return assertThrows(ResponseStatusException.class, accion).getStatusCode().value();
    }

    @Test
    void desactivaYReactivaUnaEspecialidad() {
        var e = nueva("Cardiología");
        assertTrue(e.estado());

        assertFalse(servicio.cambiarEstado(CENTRO, e.id(), false).estado());
        assertTrue(servicio.listar(CENTRO, false).stream().noneMatch(x -> x.id().equals(e.id())));
        assertTrue(servicio.listar(CENTRO, true).stream().anyMatch(x -> x.id().equals(e.id())));

        assertTrue(servicio.cambiarEstado(CENTRO, e.id(), true).estado());
        assertTrue(servicio.listar(CENTRO, false).stream().anyMatch(x -> x.id().equals(e.id())));
    }

    @Test
    void noPermiteHorarioEnEspecialidadInactiva() {
        var e = nueva("Pediatría");
        servicio.cambiarEstado(CENTRO, e.id(), false);
        var horario = new HorarioRequest(1, LocalTime.of(8, 0), LocalTime.of(12, 0));
        assertEquals(409, codigoHttp(() -> servicio.agregarHorario(CENTRO, e.id(), horario)));
    }

    @Test
    void noPermiteDarDeBajaConMedicosActivos() {
        var e = nueva("Dermatología");
        medicoServicio.registrar(CENTRO, new MedicoRequest("Ana Ruiz", "CMP-TEST-001", e.id()));
        assertEquals(409, codigoHttp(() -> servicio.cambiarEstado(CENTRO, e.id(), false)));
    }

    @Test
    void medicoNoPuedeRegistrarseEnEspecialidadInactiva() {
        var e = nueva("Urología");
        servicio.cambiarEstado(CENTRO, e.id(), false);
        assertEquals(409, codigoHttp(() ->
                medicoServicio.registrar(CENTRO, new MedicoRequest("Luis Paz", "CMP-TEST-002", e.id()))));
    }

    @Test
    void rechazaNombreDuplicadoEnElMismoCentro() {
        nueva("Neurología");
        assertEquals(409, codigoHttp(() -> nueva("neurología")));
    }

    @Test
    void validaBloquesHorarios() {
        var e = nueva("Oftalmología");
        servicio.agregarHorario(CENTRO, e.id(), new HorarioRequest(1, LocalTime.of(8, 0), LocalTime.of(12, 0)));

        // solapado -> 409
        assertEquals(409, codigoHttp(() -> servicio.agregarHorario(CENTRO, e.id(),
                new HorarioRequest(1, LocalTime.of(11, 0), LocalTime.of(14, 0)))));
        // contiguo -> permitido
        assertDoesNotThrow(() -> servicio.agregarHorario(CENTRO, e.id(),
                new HorarioRequest(1, LocalTime.of(12, 0), LocalTime.of(14, 0))));
        // fin antes que inicio -> 400
        assertEquals(400, codigoHttp(() -> servicio.agregarHorario(CENTRO, e.id(),
                new HorarioRequest(2, LocalTime.of(15, 0), LocalTime.of(9, 0)))));
        // día inválido -> 400
        assertEquals(400, codigoHttp(() -> servicio.agregarHorario(CENTRO, e.id(),
                new HorarioRequest(8, LocalTime.of(9, 0), LocalTime.of(10, 0)))));
    }

    @Test
    void centroInexistenteDevuelve404() {
        assertEquals(404, codigoHttp(() -> servicio.listar(999, false)));
    }
}