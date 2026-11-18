package medicontigo.backend;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

import medicontigo.backend.seguridad.JwtService;
import medicontigo.backend.seguridad.Rol;

@SpringBootTest
class AdministradorCajaNegraTest {

    private static final String CORREO = "admin@medicontigo.com";
    private static final String CLAVE = "admin123";
    private static final String RUTA_PRIVADA = "/api/analytics/resumen";

    @Autowired WebApplicationContext contexto;
    @Autowired JwtService jwtService;
    MockMvc mvc;

    @SuppressWarnings("unused")
    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
    }

    private String json(String correo, String password) {
        return "{\"correo\":\"%s\",\"password\":\"%s\"}".formatted(correo, password);
    }

    private String loginAdmin() throws Exception {
        String cuerpo = mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content(json(CORREO, CLAVE)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(cuerpo, "$.token");
    }

    @Test
    void cn01LoginConCredencialesCorrectasDevuelveToken() throws Exception {
        mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content(json(CORREO, CLAVE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void cn02LoginConContrasenaIncorrectaDevuelve401() throws Exception {
        mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content(json(CORREO, "incorrecta")))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.mensaje").value("Credenciales incorrectas"))
            .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void cn03LoginConCorreoNoRegistradoDevuelve401() throws Exception {
        mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("otro@correo.com", CLAVE)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.mensaje").value("Credenciales incorrectas"));
    }

    @Test
    void cn04LoginConCamposVaciosDevuelve401() throws Exception {
        mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("", "")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void cn05LoginSinContrasenaDevuelve401() throws Exception {
        mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\":\"" + CORREO + "\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void cn06LoginConInyeccionSqlDevuelve401() throws Exception {
        mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("' OR '1'='1", "' OR '1'='1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void cn07RutaPrivadaSinTokenDevuelve401() throws Exception {
        mvc.perform(get(RUTA_PRIVADA)).andExpect(status().isUnauthorized());
    }

    @Test
    void cn08RutaPrivadaConTokenValidoPermiteAcceso() throws Exception {
        int estado = mvc.perform(get(RUTA_PRIVADA).header("Authorization", "Bearer " + loginAdmin()))
            .andReturn().getResponse().getStatus();
        assertNotEquals(401, estado);
        assertNotEquals(403, estado);
    }

    @Test
    void cn09RutaPrivadaConTokenAlteradoDevuelve401() throws Exception {
        String token = loginAdmin();
        String alterado = token.substring(0, token.length() - 3) + "abc";
        mvc.perform(get(RUTA_PRIVADA).header("Authorization", "Bearer " + alterado))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void cn10RutaPrivadaConTokenVencidoDevuelve401() throws Exception {
        var generar = JwtService.class.getDeclaredMethod(
            "generar", String.class, Rol.class, Map.class, long.class);
        generar.setAccessible(true);
        String vencido = (String) generar.invoke(jwtService, CORREO, Rol.ADMIN, Map.of(), -1_000L);
        mvc.perform(get(RUTA_PRIVADA).header("Authorization", "Bearer " + vencido))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Tu sesión ha expirado. Inicia sesión nuevamente"));
    }

    @Test
    void cn11RutaPrivadaConCabeceraSinBearerDevuelve401() throws Exception {
        mvc.perform(get(RUTA_PRIVADA).header("Authorization", loginAdmin()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void cn12RutaPrivadaConTokenDePacienteDevuelve403() throws Exception {
        String token = jwtService.generarTokenPaciente(1L, "paciente@correo.com");
        mvc.perform(get(RUTA_PRIVADA).header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }
}
