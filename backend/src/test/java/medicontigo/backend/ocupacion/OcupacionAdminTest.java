package medicontigo.backend.ocupacion;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

/**
 * HU-10 (10.4): el administrador actualiza la ocupación y la vista pública
 * (mapa y perfil del centro) lo refleja de inmediato.
 */
@SpringBootTest
@Transactional
class OcupacionAdminTest {

    private static final int CENTRO = 1;       // Hospital Lima: BAJO en data.sql
    private static final int OTRO_CENTRO = 2;  // Clínica San Isidro Salud: MEDIO en data.sql
    private static final String CENTRO_INEXISTENTE = "9999";

    private static final String MAPA = "/api/centros?lat=-12.046374&lng=-77.042793&radio=40000";
    private static final String MAPA_FILTRADO = "/api/centros/filtrados?lat=-12.046374&lng=-77.042793&radio=40000";

    @Autowired WebApplicationContext contexto;
    MockMvc mvc;
    String tokenAdmin;

    @BeforeEach
    void preparar() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();

        String respuesta = mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"admin@medicontigo.com\", \"password\": \"admin123\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        tokenAdmin = JsonPath.read(respuesta, "$.token");
    }

    // ---------- ayudas ----------

    private ResultActions actualizar(Object centroId, String nivelJson, String token) throws Exception {
        var peticion = patch("/api/admin/centros/" + centroId + "/ocupacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nivelJson);
        if (token != null) {
            peticion.header("Authorization", "Bearer " + token);
        }
        return mvc.perform(peticion);
    }

    private ResultActions actualizarComoAdmin(Object centroId, String nivel) throws Exception {
        return actualizar(centroId, "{\"nivel\": \"" + nivel + "\"}", tokenAdmin);
    }

    private ResultActions perfilPublico(int centroId) throws Exception {
        return mvc.perform(get("/api/publico/centros/" + centroId));
    }

    private void verificarVistaPublica(int centroId, String nivel) throws Exception {
        perfilPublico(centroId)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nivelOcupacion").value(nivel));

        String enMapa = "$[?(@.id == " + centroId + ")].nivelOcupacion";
        mvc.perform(get(MAPA)).andExpect(status().isOk()).andExpect(jsonPath(enMapa, hasItem(nivel)));
        mvc.perform(get(MAPA_FILTRADO)).andExpect(status().isOk()).andExpect(jsonPath(enMapa, hasItem(nivel)));
    }

    private String nivelPublicoDe(int centroId) throws Exception {
        String cuerpo = perfilPublico(centroId).andReturn().getResponse().getContentAsString();
        return JsonPath.read(cuerpo, "$.nivelOcupacion");
    }

    // ---------- 10.2 endpoint + 10.4 sincronización inmediata ----------

    @Test
    void laVistaPublicaMuestraCadaCambioAlInstante() throws Exception {
        verificarVistaPublica(CENTRO, "BAJO");

        for (String nivel : new String[] {"MEDIO", "ALTO", "BAJO"}) {
            actualizarComoAdmin(CENTRO, nivel)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.centroId").value(CENTRO))
                .andExpect(jsonPath("$.nivelOcupacion").value(nivel));

            verificarVistaPublica(CENTRO, nivel);
        }
    }

    @Test
    void laRespuestaIncluyeEtiquetaYDescripcionParaLaInterfaz() throws Exception {
        actualizarComoAdmin(CENTRO, "ALTO")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Hospital Lima"))
            .andExpect(jsonPath("$.etiqueta").value("Ocupación alta"))
            .andExpect(jsonPath("$.descripcion").value("Colapsado"));
    }

    @Test
    void soloCambiaElCentroIndicado() throws Exception {
        String antes = nivelPublicoDe(OTRO_CENTRO);

        actualizarComoAdmin(CENTRO, "ALTO").andExpect(status().isOk());

        verificarVistaPublica(CENTRO, "ALTO");
        verificarVistaPublica(OTRO_CENTRO, antes);
    }

    @Test
    void aceptaElNivelEscritoEnMinusculas() throws Exception {
        actualizarComoAdmin(CENTRO, "alto")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nivelOcupacion").value("ALTO"));

        verificarVistaPublica(CENTRO, "ALTO");
    }

    // ---------- validaciones ----------

    @Test
    void rechazaUnNivelInvalidoYLaVistaPublicaNoCambia() throws Exception {
        actualizarComoAdmin(CENTRO, "MEDIO").andExpect(status().isOk());

        actualizarComoAdmin(CENTRO, "CRITICO")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Nivel de ocupación no válido. Usa BAJO, MEDIO o ALTO"));

        verificarVistaPublica(CENTRO, "MEDIO");
    }

    @Test
    void rechazaUnNivelVacioOAusente() throws Exception {
        actualizar(CENTRO, "{}", tokenAdmin)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Debes indicar el nivel de ocupación"));

        actualizar(CENTRO, "{\"nivel\": \"  \"}", tokenAdmin).andExpect(status().isBadRequest());
    }

    @Test
    void respondeNoEncontradoSiElCentroNoExiste() throws Exception {
        actualizarComoAdmin(CENTRO_INEXISTENTE, "ALTO")
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Centro no encontrado"));
    }

    // ---------- seguridad (HU-18) ----------

    @Test
    void sinTokenNoSePuedeActualizarLaOcupacion() throws Exception {
        actualizar(CENTRO, "{\"nivel\": \"ALTO\"}", null).andExpect(status().isUnauthorized());

        verificarVistaPublica(CENTRO, "BAJO");
    }

    @Test
    void unPacienteNoPuedeActualizarLaOcupacion() throws Exception {
        String correo = "ocupacion.paciente@correo.com";
        String clave = "Segura123";

        mvc.perform(post("/api/pacientes/registro").contentType(MediaType.APPLICATION_JSON).content("""
                {"nombreCompleto": "Paciente Ocupación", "correo": "%s", "contrasena": "%s",
                 "aceptaTratamientoDatos": true}
                """.formatted(correo, clave)))
            .andExpect(status().isCreated());

        String login = mvc.perform(post("/api/pacientes/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"%s\", \"contrasena\": \"%s\"}".formatted(correo, clave)))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String tokenPaciente = JsonPath.read(login, "$.token");

        actualizar(CENTRO, "{\"nivel\": \"ALTO\"}", tokenPaciente)
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status", is(403)));

        verificarVistaPublica(CENTRO, "BAJO");
    }
}
