package medicontigo.backend.moderacion;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import medicontigo.backend.servicio.FiltroContenidoService;

// HU-14 (14.4): pruebas unitarias del filtro automático de contenido
class FiltroContenidoServiceTest {

    private final FiltroContenidoService filtro = new FiltroContenidoService();

    @Test
    void comentarioLimpioNoGeneraAlertas() {
        assertEquals(List.of(),
            filtro.analizar("La atención fue buena y el médico explicó todo con paciencia."));
        assertEquals(List.of(), filtro.analizar("Pagué S/ 50.00 por la consulta, precio justo."));
        assertEquals(List.of(), filtro.analizar(null));
    }

    @Test
    void detectaLenguajeOfensivoSinImportarTildesNiMayusculas() {
        assertTrue(filtro.analizar("El personal es ESTÚPIDO").contains(FiltroContenidoService.LENGUAJE_OFENSIVO));
        assertTrue(filtro.analizar("Son unos inútiles, no vuelvo").contains(FiltroContenidoService.LENGUAJE_OFENSIVO));
    }

    @Test
    void noConfundePalabrasQueSoloContienenUnTerminoOfensivo() {
        // "computadora" contiene "puta" pero no es una palabra ofensiva
        assertFalse(filtro.analizar("Tenían la computadora malograda en recepción")
            .contains(FiltroContenidoService.LENGUAJE_OFENSIVO));
    }

    @Test
    void detectaDatosPersonales() {
        assertTrue(filtro.analizar("Escríbeme a juan.perez@gmail.com para más detalles")
            .contains(FiltroContenidoService.DATOS_PERSONALES));
        assertTrue(filtro.analizar("Llámame al 987 654 321 si quieres saber más")
            .contains(FiltroContenidoService.DATOS_PERSONALES));
        assertTrue(filtro.analizar("Mi DNI es 45678912, me atendieron mal")
            .contains(FiltroContenidoService.DATOS_PERSONALES));
    }

    @Test
    void detectaEnlacesExternosPeroNoTrataCorreosComoEnlace() {
        assertTrue(filtro.analizar("Mejores precios en https://otraclinica.pe/promo")
            .contains(FiltroContenidoService.ENLACE_EXTERNO));
        assertTrue(filtro.analizar("Visiten www.descuentos-salud.com hoy mismo")
            .contains(FiltroContenidoService.ENLACE_EXTERNO));
        assertFalse(filtro.analizar("Escríbeme a juan.perez@gmail.com por favor")
            .contains(FiltroContenidoService.ENLACE_EXTERNO));
    }

    @Test
    void detectaSpam() {
        assertTrue(filtro.analizar("Excelenteeeeeee atención").contains(FiltroContenidoService.SPAM));
        assertTrue(filtro.analizar("NO VAYAN A ESTE CENTRO NUNCA JAMAS").contains(FiltroContenidoService.SPAM));
        assertFalse(filtro.analizar("Muy buena atención del Dr. Pérez en UCI").contains(FiltroContenidoService.SPAM));
    }

    @Test
    void acumulaVariasAlertas() {
        List<String> alertas = filtro.analizar("Son unos idiotas!!!!!!! llamen al 999888777");
        assertTrue(alertas.containsAll(List.of(
            FiltroContenidoService.LENGUAJE_OFENSIVO,
            FiltroContenidoService.DATOS_PERSONALES,
            FiltroContenidoService.SPAM)));
    }
}
