package medicontigo.backend;

import medicontigo.backend.dto.CentroMapaResponse;
import medicontigo.backend.dto.EspecialidadRequest;
import medicontigo.backend.dto.MedicoRequest;
import medicontigo.backend.dto.PerfilCentroResponse;
import medicontigo.backend.servicio.CentroPublicoService;
import medicontigo.backend.servicio.EspecialidadService;
import medicontigo.backend.servicio.GeolocalizacionService;
import medicontigo.backend.servicio.MedicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PerfilCentroPublicoTest {

    private static final int CENTRO = 1;

    // Hospital Lima en data.sql
    private static final double LAT_LIMA = -12.046374;
    private static final double LNG_LIMA = -77.042793;

    @Autowired CentroPublicoService perfilServicio;
    @Autowired GeolocalizacionService geoServicio;
    @Autowired EspecialidadService especialidadServicio;
    @Autowired MedicoService medicoServicio;

    private boolean tieneEspecialidad(PerfilCentroResponse perfil, String nombre) {
        return perfil.especialidades().stream().anyMatch(e -> e.nombre().equals(nombre));
    }

    @Test
    void perfilConsolidadoTraeDatosEspecialidadesHorariosYPersonal() {
        PerfilCentroResponse perfil = perfilServicio.obtenerPerfil(CENTRO);

        assertEquals("Hospital Lima", perfil.nombre());
        assertNotNull(perfil.direccion());
        assertNotNull(perfil.telefono());
        assertNotNull(perfil.nivelOcupacion());
        assertFalse(perfil.especialidades().isEmpty());

        perfil.especialidades().forEach(e -> {
            assertFalse(e.horarios().isEmpty(), "Sin horarios: " + e.nombre());
            assertFalse(e.medicos().isEmpty(), "Sin personal: " + e.nombre());
        });
    }

    @Test
    void losHorariosVienenOrdenadosPorDia() {
        var general = perfilServicio.obtenerPerfil(CENTRO).especialidades().stream()
                .filter(e -> e.nombre().equals("Medicina General")).findFirst().orElseThrow();

        var dias = general.horarios().stream().map(h -> h.diaSemana()).toList();
        assertEquals(dias.stream().sorted().toList(), dias);
    }

    @Test
    void noMuestraEspecialidadesInactivas() {
        var nueva = especialidadServicio.crear(CENTRO, new EspecialidadRequest("Cardiología", null));
        assertTrue(tieneEspecialidad(perfilServicio.obtenerPerfil(CENTRO), "Cardiología"));

        especialidadServicio.cambiarEstado(CENTRO, nueva.id(), false);
        assertFalse(tieneEspecialidad(perfilServicio.obtenerPerfil(CENTRO), "Cardiología"));
    }

    @Test
    void noMuestraMedicosDadosDeBaja() {
        var esp = especialidadServicio.crear(CENTRO, new EspecialidadRequest("Urología", null));
        var medico = medicoServicio.registrar(CENTRO, new MedicoRequest("Pedro Test Perfil", "CMP-TEST-HU06", esp.id()));

        var antes = perfilServicio.obtenerPerfil(CENTRO).especialidades().stream()
                .filter(e -> e.nombre().equals("Urología")).findFirst().orElseThrow();
        assertEquals(1, antes.medicos().size());

        medicoServicio.desactivar(CENTRO, medico.id());

        var despues = perfilServicio.obtenerPerfil(CENTRO).especialidades().stream()
                .filter(e -> e.nombre().equals("Urología")).findFirst().orElseThrow();
        assertTrue(despues.medicos().isEmpty());
    }

    @Test
    void centroInexistenteDevuelve404() {
        var error = assertThrows(ResponseStatusException.class, () -> perfilServicio.obtenerPerfil(9999));
        assertEquals(404, error.getStatusCode().value());
    }

    @Test
    void elMapaDevuelveVariosCentrosOrdenadosPorDistancia() {
        List<CentroMapaResponse> centros = geoServicio.buscarCercanos(LAT_LIMA, LNG_LIMA, 10);

        assertTrue(centros.size() >= 5, "Se esperaban varios centros dentro de 10 km");
        assertEquals("Hospital Lima", centros.get(0).nombre());

        var distancias = centros.stream().map(CentroMapaResponse::distanciaKm).toList();
        assertEquals(distancias.stream().sorted().toList(), distancias);
    }

    @Test
    void elRadioLimitaLosResultados() {
        var cerca = geoServicio.buscarCercanos(LAT_LIMA, LNG_LIMA, 0.5);
        var lejos = geoServicio.buscarCercanos(LAT_LIMA, LNG_LIMA, 10);

        assertTrue(cerca.size() < lejos.size());
        assertTrue(cerca.stream().allMatch(c -> c.distanciaKm() <= 0.5));
    }

    @Test
    void rechazaUbicacionInvalida() {
        assertThrows(IllegalArgumentException.class, () -> geoServicio.buscarCercanos(120, LNG_LIMA, 5));
        assertThrows(IllegalArgumentException.class, () -> geoServicio.buscarCercanos(LAT_LIMA, LNG_LIMA, 0));
    }
}
