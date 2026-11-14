package medicontigo.backend;

import medicontigo.backend.dto.CentroMapaResponse;
import medicontigo.backend.dto.PerfilCentroResponse;
import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.repositorio.CentroMedicoRepository;
import medicontigo.backend.servicio.CentroPublicoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SaturacionCentroTest {

    private static final int CENTRO_ID = 1;

    @Autowired
    private CentroMedicoRepository centroRepository;

    @Autowired
    private CentroPublicoService centroPublicoService;

    private CentroMedico establecerOcupacion(String nivel) {
        CentroMedico centro = centroRepository.findById(CENTRO_ID)
                .orElseThrow();

        centro.setNivelOcupacion(nivel);

        return centroRepository.saveAndFlush(centro);
    }

    private void verificarOcupacionEnPerfilYMapa(String nivel) {
        CentroMedico centro = establecerOcupacion(nivel);

        PerfilCentroResponse perfil =
                centroPublicoService.obtenerPerfil(CENTRO_ID);

        CentroMapaResponse mapa =
                CentroMapaResponse.desde(centro, 0.0);

        assertEquals(nivel, perfil.nivelOcupacion());
        assertEquals(nivel, mapa.nivelOcupacion());
    }

    @Test
    void debeMostrarOcupacionBaja() {
        verificarOcupacionEnPerfilYMapa("BAJO");
    }

    @Test
    void debeMostrarOcupacionMedia() {
        verificarOcupacionEnPerfilYMapa("MEDIO");
    }

    @Test
    void debeMostrarOcupacionAlta() {
        verificarOcupacionEnPerfilYMapa("ALTO");
    }

    @Test
    void debeRechazarCentroInexistente() {
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> centroPublicoService.obtenerPerfil(9999)
        );

        assertEquals(404, error.getStatusCode().value());
    }
}
