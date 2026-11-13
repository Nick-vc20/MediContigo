package medicontigo.backend.moderacion;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

// HU-14 (14.4): flujo completo de moderación (paciente publica → admin aprueba/rechaza/oculta → vista pública)
@SpringBootTest
@Transactional
class ModeracionResenaTest {

    private static final String CORREO = "moderacion@correo.com";
    private static final String CLAVE = "Segura123";
    private static final int CENTRO = 6;

    @Autowired WebApplicationContext contexto;
    MockMvc mvc;
    String tokenPaciente;
    String tokenAdmin;

    @BeforeEach
    void preparar() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
        mvc.perform(post("/api/pacientes/registro").contentType(MediaType.APPLICATION_JSON).content("""
                {"nombreCompleto": "Rosa Moderación", "correo": "%s", "contrasena": "%s",
                 "aceptaTratamientoDatos": true}
                """.formatted(CORREO, CLAVE)))
            .andExpect(status().isCreated());

        tokenPaciente = token(mvc.perform(post("/api/pacientes/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"%s\", \"contrasena\": \"%s\"}".formatted(CORREO, CLAVE)))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        tokenAdmin = token(mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"admin@medicontigo.com\", \"password\": \"admin123\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    private static String token(String cuerpo) {
        return JsonPath.read(cuerpo, "$.token");
    }

    private Long publicar(String comentario) throws Exception {
        String cuerpo = mvc.perform(post("/api/paciente/centros/" + CENTRO + "/resenas")
                .header("Authorization", "Bearer " + tokenPaciente)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\": 4, \"comentario\": \"%s\"}".formatted(comentario)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.estado").value("PENDIENTE"))
            .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(cuerpo, "$.id")).longValue();
    }

    private org.springframework.test.web.servlet.ResultActions moderar(Long id, String json) throws Exception {
        return mvc.perform(patch("/api/admin/resenas/" + id + "/moderacion")
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json));
    }

    private org.springframework.test.web.servlet.ResultActions verPublicas() throws Exception {
        return mvc.perform(get("/api/publico/centros/" + CENTRO + "/resenas"));
    }

    // ---------- Seguridad ----------

    @Test
    void elPanelExigeTokenDeAdministrador() throws Exception {
        mvc.perform(get("/api/admin/resenas")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/resenas").header("Authorization", "Bearer " + tokenPaciente))
            .andExpect(status().isForbidden());
        mvc.perform(patch("/api/admin/resenas/1/moderacion")
                .header("Authorization", "Bearer " + tokenPaciente)
                .contentType(MediaType.APPLICATION_JSON).content("{\"accion\": \"APROBAR\"}"))
            .andExpect(status().isForbidden());
    }

    // ---------- Flujo principal ----------

    @Test
    void reseñaPendienteNoEsPublicaHastaQueElAdminLaAprueba() throws Exception {
        Long id = publicar("Me atendieron con mucha amabilidad y rapidez.");

        verPublicas().andExpect(jsonPath("$[?(@.id == %d)]".formatted(id)).isEmpty());

        mvc.perform(get("/api/admin/resenas").param("estado", "PENDIENTE").param("centroId", "" + CENTRO)
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == %d)].nombrePaciente".formatted(id)).value("Rosa Moderación"))
            .andExpect(jsonPath("$[?(@.id == %d)].alertas".formatted(id)).value(contains(empty())));

        moderar(id, "{\"accion\": \"APROBAR\"}")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("PUBLICADA"))
            .andExpect(jsonPath("$.moderadoPor").value("admin@medicontigo.com"))
            .andExpect(jsonPath("$.fechaModeracion").isNotEmpty());

        verPublicas().andExpect(jsonPath("$[?(@.id == %d)].comentario".formatted(id))
            .value("Me atendieron con mucha amabilidad y rapidez."));
    }

    @Test
    void rechazarExigeMotivoYElPacienteLoVeEnSuCuenta() throws Exception {
        Long id = publicar("Comentario que no cumple las normas del sitio.");

        moderar(id, "{\"accion\": \"RECHAZAR\"}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("motivo")));

        moderar(id, "{\"accion\": \"RECHAZAR\", \"motivo\": \"No describe una experiencia de atención\"}")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("RECHAZADA"))
            .andExpect(jsonPath("$.motivoModeracion").value("No describe una experiencia de atención"));

        verPublicas().andExpect(jsonPath("$[?(@.id == %d)]".formatted(id)).isEmpty());

        mvc.perform(get("/api/paciente/resenas").header("Authorization", "Bearer " + tokenPaciente))
            .andExpect(jsonPath("$[0].estado").value("RECHAZADA"))
            .andExpect(jsonPath("$[0].motivoModeracion").value("No describe una experiencia de atención"));
    }

    @Test
    void ocultarRetiraUnaReseñaPublicadaYAprobarLaRestaura() throws Exception {
        Long id = publicar("Buena atención en general, lo recomiendo.");
        moderar(id, "{\"accion\": \"APROBAR\"}").andExpect(status().isOk());

        moderar(id, "{\"accion\": \"OCULTAR\", \"motivo\": \"Denuncia de otro usuario en revisión\"}")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("OCULTA"));
        verPublicas().andExpect(jsonPath("$[?(@.id == %d)]".formatted(id)).isEmpty());

        moderar(id, "{\"accion\": \"APROBAR\"}")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("PUBLICADA"));
        verPublicas().andExpect(jsonPath("$[?(@.id == %d)]".formatted(id)).isNotEmpty());
    }

    // ---------- Transiciones no válidas ----------

    @Test
    void transicionesNoPermitidasDevuelven409() throws Exception {
        Long id = publicar("Atención aceptable, nada especial que comentar.");

        moderar(id, "{\"accion\": \"OCULTAR\", \"motivo\": \"Prueba de ocultar\"}")
            .andExpect(status().isConflict());

        moderar(id, "{\"accion\": \"APROBAR\"}").andExpect(status().isOk());
        moderar(id, "{\"accion\": \"APROBAR\"}").andExpect(status().isConflict());
        moderar(id, "{\"accion\": \"RECHAZAR\", \"motivo\": \"Ya estaba publicada\"}")
            .andExpect(status().isConflict());
    }

    @Test
    void accionInvalidaOReseñaInexistente() throws Exception {
        Long id = publicar("Atención aceptable, nada especial que comentar.");
        moderar(id, "{\"accion\": \"BORRAR\"}").andExpect(status().isBadRequest());
        moderar(id, "{}").andExpect(status().isBadRequest());
        moderar(999_999L, "{\"accion\": \"APROBAR\"}").andExpect(status().isNotFound());
        mvc.perform(get("/api/admin/resenas").param("estado", "BORRADA")
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isBadRequest());
    }

    // ---------- Filtro de contenido ----------

    @Test
    void elFiltroMarcaAlertasYExigeConfirmacionParaPublicar() throws Exception {
        Long id = publicar("Son unos idiotas, escríbanme a queja@correo.com");

        mvc.perform(get("/api/admin/resenas").param("conAlertas", "true")
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == %d)].alertas[*]".formatted(id),
                containsInAnyOrder("LENGUAJE_OFENSIVO", "DATOS_PERSONALES")));

        moderar(id, "{\"accion\": \"APROBAR\"}")
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(containsString("LENGUAJE_OFENSIVO")));
        verPublicas().andExpect(jsonPath("$[?(@.id == %d)]".formatted(id)).isEmpty());

        moderar(id, "{\"accion\": \"APROBAR\", \"confirmarAlertas\": true}")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("PUBLICADA"));
    }

    @Test
    void comentarioLimpioNoApareceEnElFiltroDeAlertas() throws Exception {
        Long id = publicar("Excelente trato del personal de enfermería.");
        mvc.perform(get("/api/admin/resenas").param("conAlertas", "true")
                .header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(jsonPath("$[?(@.id == %d)]".formatted(id)).isEmpty());
    }

    // ---------- Resumen ----------

    @Test
    void elResumenCuentaPorEstado() throws Exception {
        String antes = mvc.perform(get("/api/admin/resenas/resumen").header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        int pendientes = JsonPath.read(antes, "$.pendientes");
        int conAlertas = JsonPath.read(antes, "$.pendientesConAlertas");
        int publicadas = JsonPath.read(antes, "$.publicadas");

        Long limpia = publicar("Muy buena experiencia con el área de triaje.");
        publicar("Atención pésima, son unos inútiles.");
        moderar(limpia, "{\"accion\": \"APROBAR\"}").andExpect(status().isOk());

        mvc.perform(get("/api/admin/resenas/resumen").header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(jsonPath("$.pendientes").value(pendientes + 1))
            .andExpect(jsonPath("$.pendientesConAlertas").value(conAlertas + 1))
            .andExpect(jsonPath("$.publicadas").value(publicadas + 1));
    }
}
