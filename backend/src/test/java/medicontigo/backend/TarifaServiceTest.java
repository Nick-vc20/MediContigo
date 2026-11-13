package medicontigo.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.TarifaRequest;
import medicontigo.backend.dto.TarifaResponse;
import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.modelo.Especialidad;
import medicontigo.backend.modelo.Tarifa;
import medicontigo.backend.repositorio.CentroMedicoRepository;
import medicontigo.backend.repositorio.EspecialidadRepository;
import medicontigo.backend.repositorio.TarifaRepository;
import medicontigo.backend.servicio.TarifaService;

class TarifaServiceTest {

    private TarifaRepository tarifaRepository;
    private EspecialidadRepository especialidadRepository;
    private CentroMedicoRepository centroMedicoRepository;
    private TarifaService tarifaService;

    private CentroMedico centro;
    private Especialidad especialidad;

    @BeforeEach
    void preparar() {

        tarifaRepository = mock(TarifaRepository.class);
        especialidadRepository = mock(EspecialidadRepository.class);
        centroMedicoRepository = mock(CentroMedicoRepository.class);

        tarifaService = new TarifaService(
                tarifaRepository,
                especialidadRepository,
                centroMedicoRepository
        );

        centro = new CentroMedico(
                1,
                "Centro Medico de Prueba",
                "Clinica",
                "Lima"
        );

        especialidad = new Especialidad();
        especialidad.setCentro(centro);
        especialidad.setNombre("Medicina General");

        when(centroMedicoRepository.existsById(1)).thenReturn(true);

        when(especialidadRepository.findById(1))
                .thenReturn(Optional.of(especialidad));
    }

    @Test
    void debeRechazarPrecioNegativo() {

        TarifaRequest solicitud =
                new TarifaRequest(new BigDecimal("-10.00"));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> tarifaService.guardar(1, 1, solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(tarifaRepository, never()).saveAndFlush(any(Tarifa.class));
    }

    @Test
    void debeRechazarPrecioNulo() {

        TarifaRequest solicitud = new TarifaRequest(null);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> tarifaService.guardar(1, 1, solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(tarifaRepository, never()).saveAndFlush(any(Tarifa.class));
    }

    @Test
    void debeRechazarMasDeDosDecimales() {

        TarifaRequest solicitud =
                new TarifaRequest(new BigDecimal("50.123"));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> tarifaService.guardar(1, 1, solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(tarifaRepository, never()).saveAndFlush(any(Tarifa.class));
    }

    @Test
    void debeRechazarPrecioFueraDeRango() {

        TarifaRequest solicitud =
                new TarifaRequest(new BigDecimal("999999999.00"));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> tarifaService.guardar(1, 1, solicitud)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(tarifaRepository, never()).saveAndFlush(any(Tarifa.class));
    }

    @Test
    void debeRegistrarTarifaValida() {

        TarifaRequest solicitud =
                new TarifaRequest(new BigDecimal("80.00"));

        when(tarifaRepository.findByEspecialidadId(1))
                .thenReturn(Optional.empty());

        when(tarifaRepository.saveAndFlush(any(Tarifa.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        TarifaResponse respuesta =
                tarifaService.guardar(1, 1, solicitud);

        assertEquals(
                new BigDecimal("80.00"),
                respuesta.precioAproximado()
        );
        assertEquals("PEN", respuesta.moneda());
        assertEquals(1, respuesta.centroId());

        verify(tarifaRepository, times(1))
                .saveAndFlush(any(Tarifa.class));
    }

    @Test
    void debeActualizarTarifaExistente() {

        Tarifa tarifaExistente = new Tarifa();
        tarifaExistente.setEspecialidad(especialidad);
        tarifaExistente.setPrecioAproximado(new BigDecimal("50.00"));

        when(tarifaRepository.findByEspecialidadId(1))
                .thenReturn(Optional.of(tarifaExistente));

        when(tarifaRepository.saveAndFlush(any(Tarifa.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        TarifaResponse respuesta = tarifaService.guardar(
                1,
                1,
                new TarifaRequest(new BigDecimal("80.00"))
        );

        assertEquals(
                new BigDecimal("80.00"),
                respuesta.precioAproximado()
        );

        assertSame(
                tarifaExistente,
                tarifaRepository.findByEspecialidadId(1).orElseThrow()
        );

        verify(tarifaRepository, times(1))
                .saveAndFlush(tarifaExistente);
    }

    @Test
    void debeRechazarEspecialidadInexistente() {

        when(especialidadRepository.findById(99))
                .thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> tarifaService.guardar(
                        1,
                        99,
                        new TarifaRequest(new BigDecimal("50.00"))
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verify(tarifaRepository, never()).saveAndFlush(any(Tarifa.class));
    }

    @Test
    void debeRechazarEspecialidadDeOtroCentro() {

        CentroMedico otroCentro = new CentroMedico(
                2,
                "Otro Centro",
                "Clinica",
                "Lima"
        );

        Especialidad especialidadAjena = new Especialidad();
        especialidadAjena.setCentro(otroCentro);
        especialidadAjena.setNombre("Cardiologia");

        when(especialidadRepository.findById(2))
                .thenReturn(Optional.of(especialidadAjena));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> tarifaService.guardar(
                        1,
                        2,
                        new TarifaRequest(new BigDecimal("50.00"))
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(tarifaRepository, never()).saveAndFlush(any(Tarifa.class));
    }
}
