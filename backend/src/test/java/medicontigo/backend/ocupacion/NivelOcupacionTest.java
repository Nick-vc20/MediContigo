package medicontigo.backend.ocupacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import medicontigo.backend.modelo.NivelOcupacion;

// HU-10 (10.4): reglas del nivel de ocupación, sin levantar Spring
class NivelOcupacionTest {

    @Test
    void aceptaLosTresNivelesValidos() {
        assertEquals(NivelOcupacion.BAJO, NivelOcupacion.desde("BAJO"));
        assertEquals(NivelOcupacion.MEDIO, NivelOcupacion.desde("MEDIO"));
        assertEquals(NivelOcupacion.ALTO, NivelOcupacion.desde("ALTO"));
    }

    @Test
    void ignoraMayusculasYEspacios() {
        assertEquals(NivelOcupacion.ALTO, NivelOcupacion.desde("alto"));
        assertEquals(NivelOcupacion.MEDIO, NivelOcupacion.desde("  Medio "));
    }

    @Test
    void rechazaNivelesDesconocidos() {
        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> NivelOcupacion.desde("CRITICO"));
        assertEquals("Nivel de ocupación no válido. Usa BAJO, MEDIO o ALTO", e.getMessage());
    }

    @Test
    void rechazaNivelNuloOVacio() {
        assertThrows(IllegalArgumentException.class, () -> NivelOcupacion.desde(null));
        assertThrows(IllegalArgumentException.class, () -> NivelOcupacion.desde("   "));
    }

    @Test
    void cadaNivelTieneEtiquetaYDescripcion() {
        for (NivelOcupacion nivel : NivelOcupacion.values()) {
            assertFalse(nivel.getEtiqueta().isBlank());
            assertFalse(nivel.getDescripcion().isBlank());
        }
    }
}
